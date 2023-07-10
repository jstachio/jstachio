package io.jstach.jstachio.spi;

import static io.jstach.jstachio.spi.Templates.sneakyThrow;
import static io.jstach.jstachio.spi.Templates.validateEncoding;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Output;
import io.jstach.jstachio.Output.EncodedOutput;
import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateInfo;
import io.jstach.jstachio.TemplateModel;
import io.jstach.jstachio.spi.JStachioFilter.FilterChain;

/**
 * An abstract jstachio that just needs a {@link JStachioExtensions} container.
 * <p>
 * To extend just override {@link #extensions()}.
 *
 * @see JStachioExtensions
 * @author agentgt
 */
public abstract class AbstractJStachio implements JStachio, JStachioExtensions.Provider {

	/**
	 * Do nothing constructor
	 */
	public AbstractJStachio() {

	}

	@Override
	public <A extends Output<E>, E extends Exception> A execute(Object model, A appendable) throws E {
		var t = _findTemplate(model);
		return t.execute(model, appendable);
	}

	@Override
	public <A extends EncodedOutput<E>, E extends Exception> A write(Object model, A appendable) throws E {
		var t = _findTemplate(model);
		validateEncoding(t, appendable);
		return t.write(model, appendable);
	}

	/*
	 * IF YOU want this method to be not final please file a bug.
	 */
	@Override
	public final Template<Object> findTemplate(Object model) {
		return _findTemplate(model);
	}

	/*
	 * IF YOU want this method protected (thus overrideable) please file a bug.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Template<Object> _findTemplate(Object model) {
		TemplateInfo template;
		if (model instanceof TemplateModel te) {
			/*
			 * TemplateExecutables can execute on themselves.
			 */
			template = te.template();
		}
		else {
			template = template(model.getClass());
		}
		var filter = loadFilter(model, template);
		Template t = FilterChain.toTemplate(filter, template);
		return t;
	}

	/**
	 * Loads the filter and checks if it can process the model and template.
	 * @param model to render
	 * @param template loaded by {@link #template(Class)}
	 * @return filter chain that can process model
	 */
	protected final FilterChain loadFilter(Object model, TemplateInfo template) {
		var filter = FilterChain.of(extensions().getFilter(), template);
		if (filter.isBroken(model)) {
			boolean isReflectiveTemplate = Templates.isReflectionTemplate(template);
			final String ind = "\n\t";
			String reason = "";
			if (isReflectiveTemplate) {
				reason = " This is usually because the template "
						+ "has not been compiled and reflection based rendering is not available.";
			}
			throw new BrokenFilterException( //
					"Filter chain unable to process template/model." + reason //
							+ ind + "template: \"" + template.description() + "\"" //
							+ ind + "model type: \"" + model.getClass() + "\"" //
							+ ind + "reflection used: \"" + isReflectiveTemplate + "\"");
		}
		return filter;
	}

	/*
	 * IF YOU want this method to be not final please file a bug.
	 */
	@Override
	public final boolean supportsType(Class<?> modelType) {
		if (TemplateModel.class.isAssignableFrom(modelType)) {
			return true;
		}
		return extensions().getTemplateFinder().supportsType(modelType);
	}

	/**
	 * Finds the template by model class wrapping any exceptions.
	 * @param modelType the class of the model.
	 * @return found template never <code>null</code>.
	 */
	protected TemplateInfo template(Class<?> modelType) {
		try {
			return extensions().getTemplateFinder().findTemplate(modelType);
		}
		catch (Exception e) {
			sneakyThrow(e);
			throw new RuntimeException(e);
		}
	}

}

class DefaultJStachio extends AbstractJStachio {

	private final JStachioExtensions extensions;

	public DefaultJStachio(JStachioExtensions extensions) {
		super();
		this.extensions = extensions;
	}

	@Override
	public JStachioExtensions extensions() {
		return this.extensions;
	}

}
