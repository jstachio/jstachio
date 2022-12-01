package io.jstach.jstachio.spi;

import java.io.IOException;
import java.io.UncheckedIOException;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.TemplateInfo;

/**
 * An abstract jstachio that just needs a {@link JStachioTemplateFinder} and
 * {@link JStachioFilter}.
 *
 * @author agentgt
 */
public abstract class AbstractJStachio implements JStachio {

	@Override
	public void execute(Object model, Appendable appendable) throws IOException {
		TemplateInfo template = template(model.getClass());
		filter().filter(template).process(model, appendable);
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
		return templateFinder().supportsType(modelType);
	}

	@SuppressWarnings("unchecked")
	static <E extends Throwable> void sneakyThrow(final Throwable x) throws E {
		throw (E) x;
	}

	protected TemplateInfo template(Class<?> modelType) {
		try {
			return templateFinder().findTemplate(modelType);
		}
		catch (Exception e) {
			sneakyThrow(e);
			throw new RuntimeException(e);
		}
	}

	protected abstract JStachioTemplateFinder templateFinder();

	protected abstract JStachioFilter filter();

}
