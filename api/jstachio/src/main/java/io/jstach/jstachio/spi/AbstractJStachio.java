package io.jstach.jstachio.spi;

import static io.jstach.jstachio.spi.Templates.sneakyThrow;

import java.io.IOException;
import java.io.UncheckedIOException;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.TemplateInfo;
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
	public void execute(Object model, Appendable appendable) throws IOException {
		TemplateInfo template = template(model.getClass());
		var filter = loadFilter(model, template);
		filter.process(model, appendable);

	}

	/**
	 * Loads the filter and checks if it can process the model and template.
	 * @param model to render
	 * @param template loaded by {@link #template(Class)}
	 * @return filter chain that can process model
	 * @throws IOException if the filter cannot process the model
	 */
	protected FilterChain loadFilter(Object model, TemplateInfo template) throws IOException {
		var filter = extensions().getFilter().filter(template);
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

	@Override
	public StringBuilder execute(Object model, StringBuilder sb) {
		try {
			execute(model, (Appendable) sb);
			return sb;
		}
		catch (IOException e) {
			sneakyThrow(e);
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public String execute(Object model) {
		return execute(model, new StringBuilder()).toString();
	}

	@Override
	public boolean supportsType(Class<?> modelType) {
		return extensions().getTemplateFinder().supportsType(modelType);
	}

	/**
	 * Finds the template by model class
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
