package io.jstach.jstachio.spi;

import java.io.IOException;
import java.io.UncheckedIOException;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.TemplateInfo;
import static io.jstach.jstachio.spi.Templates.sneakyThrow;

/**
 * An abstract jstachio that just needs a {@link JStachioTemplateFinder} and
 * {@link JStachioFilter}.
 *
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
		extensions().getFilter().filter(template).process(model, appendable);
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
