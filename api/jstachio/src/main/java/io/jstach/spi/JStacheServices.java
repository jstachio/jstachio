package io.jstach.spi;

import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.RenderFunction;
import io.jstach.Renderer;
import io.jstach.TemplateInfo;
import io.jstach.annotation.JStache;

/**
 * The SPI extension point. All methods are optional (default).
 *
 * TODO this still in the works and subject to change greatly!
 *
 * @author agentgt
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
	default RenderFunction filter( //
			TemplateInfo template, //
			Object context, //
			RenderFunction previous) {
		return previous;
	}

	/**
	 * Provide a config or not. The final config is a composite of all the found configs.
	 * @apiNote This method is called before {@link #init(JStacheConfig)}
	 * @return config if this service provides one or <code>null</code>
	 */
	default @Nullable JStacheConfig provideConfig() {
		return null;
	}

	/**
	 * Called before the services are used but after {@link #provideConfig()}.
	 * @param config the config never null
	 */
	default void init(JStacheConfig config) {

	}

	/**
	 * Find the root service which is an aggregate of all found implementations.
	 * @return the root service never <code>null</code>.
	 */
	public static JStacheServices find() {
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

	/**
	 * Resolve template information if possible.
	 * <p>
	 * Implementations are only called if the compiled renderers is missing.
	 * @param contextType models class.
	 * @return resolved template info
	 * @throws Exception if any problem happens creating template information
	 */
	public static TemplateInfo templateInfo(Class<?> contextType) throws Exception {
		return JStacheServicesResolver._templateInfo(contextType);
	}

	/**
	 * Finds a renderer for a model class.
	 * @param <T> the type of model.
	 * @param modelType the class of the model (annotated with {@link JStache})
	 * @return renderer for the specific type.
	 * @throws RuntimeException if the renderer is not found for the type.
	 */
	public static <T> Renderer<T> renderer(Class<T> modelType) {
		return JStacheServicesResolver._renderer(modelType);
	}

}
