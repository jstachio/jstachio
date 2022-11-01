package io.jstach.spi;

import java.util.Optional;
import java.util.stream.Stream;

import io.jstach.Appender;
import io.jstach.RenderFunction;
import io.jstach.Renderer;
import io.jstach.TemplateInfo;
import io.jstach.annotation.JStache;

/**
 * The SPI extension point. All methods are optional (default). TODO this still in the
 * works and subject to change greatly
 *
 * @author agent
 *
 */
public interface JStacheServices {

	/**
	 * Advises or filters a previously applied template and model like a filter chain.
	 * @param template info about the template
	 * @param context the root model
	 * @param previous the function returned early in the chain.
	 * @return an advised render function or often the previous render function if no
	 * advise is needed.
	 */
	default RenderFunction filter(TemplateInfo template, Object context, RenderFunction previous) {
		return previous;
	}

	/**
	 * The root appender where all escaped and formatted data are passed through.
	 * @return the default do nothing appender
	 */
	default Appender<Appendable> appender() {
		return Appender.DefaultAppender.INSTANCE;
	}

	/**
	 * Finds a renderer for a model class.
	 * @param <T> the type of model.
	 * @param modelType the class of the model (annotated with {@link JStache})
	 * @return renderer for the specifi type.
	 * @throws RuntimeException if the renderer is not found for the type.
	 */
	public static <T> Renderer<T> renderer(Class<T> modelType) {
		return JStacheServicesResolver._renderer(modelType);
	}

	/**
	 * Find the root service which is an aggregate of all found implementations.
	 * @return the root service never <code>null</code>.
	 */
	public static JStacheServices findService() {
		return JStacheServicesResolver.INSTANCE;
	}

	/**
	 * Find all implementations minus the root aggregate.
	 * @return all custom implementations.
	 */
	public static Stream<JStacheServices> findAll() {
		return JStacheServicesResolver._services();
	}

	/**
	 * Finds a specific implementation using {@link Class#isAssignableFrom(Class)}.
	 * @param <T> the implementation type
	 * @param c the implementation type.
	 * @return an implementation if found
	 */
	public static <T extends JStacheServices> Optional<T> find(Class<T> c) {
		return findAll().filter(s -> c.isAssignableFrom(s.getClass())).map(c::cast).findFirst();
	}

}
