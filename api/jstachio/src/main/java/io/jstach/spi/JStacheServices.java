package io.jstach.spi;

import java.util.Optional;
import java.util.stream.Stream;

import io.jstach.Appender;
import io.jstach.RenderFunction;
import io.jstach.Renderer;
import io.jstach.TemplateInfo;

/**
 * TODO this still in the works and subject to change greatly
 *
 * @author agent
 *
 */
public interface JStacheServices {

	default RenderFunction filter(TemplateInfo template, Object context, RenderFunction previous) {
		return previous;
	}

	default Appender<Appendable> appender() {
		return Appender.DefaultAppender.INSTANCE;
	}

	public static <T> Renderer<T> renderer(Class<T> modelType) {
		return JStacheServicesResolver._renderer(modelType);
	}

	public static JStacheServices findService() {
		return JStacheServicesResolver.INSTANCE;
	}

	public static Stream<JStacheServices> findAll() {
		return JStacheServicesResolver._services();
	}

	public static <T extends JStacheServices> Optional<T> find(Class<T> c) {
		return findAll().filter(s -> c.isAssignableFrom(s.getClass())).map(c::cast).findFirst();
	}

}
