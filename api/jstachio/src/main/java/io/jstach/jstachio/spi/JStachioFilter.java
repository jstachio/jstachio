package io.jstach.jstachio.spi;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Output;
import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateInfo;

/**
 * Advises, intercepts or filters a template before being rendered.
 * <p>
 * This extension point is largely to support dynamic updates of templates where a
 * template is being edited while the JVM is fully loaded and we need to intercept the
 * call to provide rendering of the updated template.
 * <p>
 * The extension will only be executed if {@link JStachio} render (and execute) methods
 * are used and not the generated classes render methods.
 * <p>
 * When JStachio renders a model through the runtime it:
 * <ol>
 * <li>Loads the template. In some cases it may use reflection and thus
 * {@link TemplateInfo} may not be a generated {@link Template}.</li>
 * <li>Loads the composite filter which is all the filters combined in order (see
 * {@link #order()}).</li>
 * <li>{@linkplain FilterChain#of(JStachioFilter, TemplateInfo) Creates the filter chain}
 * with the loaded template.</li>
 * <li>Then tells the chain to {@linkplain FilterChain#process(Object, Output) process}
 * the rendering.</li>
 * </ol>
 *
 * @apiNote <strong class="warn"> &#x26A0; WARNING! While this extension point is public
 * API it is recommended you do not use it.</strong> It is less stable than the rest of
 * the API and is subject to change in the future. Implementations should be threadsafe!
 * @author agentgt
 *
 */
public non-sealed interface JStachioFilter extends JStachioExtension {

	/**
	 * A fully composed chain that renders a model by applying filtering.
	 *
	 * @apiNote The filter chain should be stateless and threadsafe as there is no
	 * guarantee that a filter chain will be recreated for the same {@link TemplateInfo}.
	 * @author agentgt
	 *
	 */
	public interface FilterChain {

		/**
		 * Renders the passed in model.
		 * @param model a model assumed never to be <code>null</code>.
		 * @param appendable the appendable to write to.
		 * @throws IOException if there is an error writing to the appendable
		 */
		public void process(Object model, Output<?> appendable) throws Exception;

		/**
		 * A marker method that the filter is broken and should not be used. This mainly
		 * for the filter pipeline to determine if filter should be called.
		 * @param model the model that would be rendered
		 * @return by default false
		 */
		default boolean isBroken(Object model) {
			return false;
		}

		/**
		 * Converts the filter chain into a template if it is not already one.
		 * @param chain process will be used when the template is executed.
		 * @param templateInfo template info to use if the filter chain is not a template.
		 * @return chain as a template
		 */
		public static Template<?> toTemplate(FilterChain chain, TemplateInfo templateInfo) {
			if (chain instanceof Template<?> template) {
				return template;
			}
			return new Template<Object>() {

				@Override
				public String templateName() {
					return templateInfo.templateName();
				}

				@Override
				public String templatePath() {
					return templateInfo.templatePath();
				}

				@Override
				public Class<?> templateContentType() {
					return templateInfo.templateContentType();
				}

				@Override
				public Charset templateCharset() {
					return templateInfo.templateCharset();
				}

				@Override
				public String templateMediaType() {
					return templateInfo.templateMediaType();
				}

				@Override
				public Function<String, String> templateEscaper() {
					return templateInfo.templateEscaper();
				}

				@Override
				public Function<@Nullable Object, String> templateFormatter() {
					return templateInfo.templateFormatter();
				}

				@Override
				public boolean supportsType(Class<?> type) {
					return templateInfo.supportsType(type);
				}

				@Override
				public <A extends Output<E>, E extends Exception> A execute(Object model, A appendable) throws E {
					try {
						chain.process(model, appendable);
						return appendable;
					}
					catch (Exception e) {
						Templates.sneakyThrow(e);
						throw new RuntimeException(e);
					}
				}

				@Override
				public Class<?> modelClass() {
					return templateInfo.modelClass();
				}

			};
		}

		/**
		 * Create the filter chain from the filter and a template by resolving the first
		 * filter.
		 * <p>
		 * The first filter (previous) will be broken unless the parameter template is a
		 * {@link FilterChain} which generated renderers usually are.
		 * @param filter usually the composite filter
		 * @param template info about the template
		 * @return an advised render function or often the previous render function if no
		 * advise is needed.
		 */
		@SuppressWarnings("unchecked")
		public static FilterChain of( //
				JStachioFilter filter, TemplateInfo template) {
			FilterChain previous = BrokenFilter.INSTANCE;
			if (template instanceof FilterChain c) {
				previous = c;
			}
			else if (template instanceof Template t) {
				/*
				 * This is sort of abusing that filter chains happen to be a functional
				 * interface
				 */
				previous = (model, appendable) -> {
					t.execute(model, appendable);
				};
			}
			return filter.filter(template, previous);
		}

	}

	/**
	 * Advises or filters a previously created filter.
	 * @param template info about the template
	 * @param previous the function returned early in the chain.
	 * @return an advised render function or often the previous render function if no
	 * advise is needed.
	 */
	FilterChain filter( //
			TemplateInfo template, //
			FilterChain previous);

	/**
	 * Hint on order of filter chain. The found {@link JStachioFilter}s are sorted
	 * naturally (lower number comes first) based on the returned number. Thus the filter
	 * that has the greatest say is the filter with the highest number.
	 * @return default returns zero
	 */
	default int order() {
		return 0;
	}

	/**
	 * Creates a composite filter of a many filters.
	 * @param filters not null.
	 * @return a composite filter ordered by {@link JStachioFilter#order()}
	 */
	public static JStachioFilter compose(Iterable<JStachioFilter> filters) {
		List<JStachioFilter> fs = new ArrayList<>();
		for (var f : filters) {
			fs.add(f);
		}
		if (fs.isEmpty()) {
			return NoFilter.NO_FILTER;
		}
		if (fs.size() == 1) {
			return fs.get(0);
		}
		fs.sort(Comparator.comparingInt(JStachioFilter::order));
		return new CompositeFilterChain(List.copyOf(fs));
	}

}

/**
 * Thrown if process is called on a broken filter. Currently this is a private API.
 *
 * @author agentgt
 */
class BrokenFilterException extends RuntimeException {

	private static final long serialVersionUID = -1206760388422768739L;

	/**
	 * Invoked if filter is brken
	 * @param s message
	 */
	public BrokenFilterException(String s) {
		super(s);
	}

}

enum BrokenFilter implements io.jstach.jstachio.spi.JStachioFilter.FilterChain {

	INSTANCE;

	@Override
	public void process(Object model, Output<?> appendable) {
		throw new BrokenFilterException("Unable to process model: " + model.getClass().getName()
				+ " probably because a template could not be found.");
	}

	@Override
	public boolean isBroken(Object model) {
		return true;
	}

}

enum NoFilter implements JStachioFilter {

	NO_FILTER;

	@Override
	public FilterChain filter(TemplateInfo template, FilterChain previous) {
		return previous;
	}

}

class CompositeFilterChain implements JStachioFilter {

	private final List<JStachioFilter> filters;

	public CompositeFilterChain(List<JStachioFilter> filters) {
		super();
		this.filters = filters;
	}

	@Override
	public FilterChain filter(TemplateInfo template, FilterChain previous) {
		var current = previous;
		for (var f : filters) {
			current = f.filter(template, current);
		}
		return current;
	}

}
