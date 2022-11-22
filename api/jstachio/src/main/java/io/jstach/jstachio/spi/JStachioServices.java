package io.jstach.jstachio.spi;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstachio.JStachio;

/**
 * An SPI extension point via the {@link ServiceLoader} that is a factory that provides
 * services. All methods are optional (default) so that implementations can decide what
 * particularly plugins/services they want to provide.
 *
 * @author agentgt
 *
 */
public interface JStachioServices {

	/**
	 * Provide a filter or not. The final filter is a composite and becomes a filter
	 * chain.
	 * @return filter if this service provider provies one or <code>null</code>
	 */
	default @Nullable JStachioFilter provideFilter() {
		return null;
	}

	/**
	 * Provide a config or not. The final config is a composite of all the found configs.
	 * <p>
	 * Specifically if multiple instances of {@link JStachioServices} are found that
	 * return a nonnull they will be combined by looping through all of them to find a
	 * nonnull value for {@link JStachioConfig#getProperty(String)}. If no configs are
	 * provided or no services found the root {@link JStachioServices} instance will use
	 * {@link System#getProperties()}.
	 *
	 * @apiNote This method is called before {@link #init(JStachioConfig)}
	 * @return config if this service provides one or <code>null</code>
	 */
	default @Nullable JStachioConfig provideConfig() {
		return null;
	}

	/**
	 * Provide a template finder or not. Only template finder is allowed and if multiples
	 * are found other than the default a {@link RuntimeException} will be thrown.
	 * @return template finder or not
	 */
	default @Nullable JStachioTemplateFinder provideTemplateFinder() {
		return null;
	}

	/**
	 * Provide a rendering engine or not. Only one JStachio is allowed and if multiples
	 * are found other than the default a {@link RuntimeException} will be thrown.
	 * @return jstachio rendering engine
	 */
	default @Nullable JStachio provideJStachio() {
		return null;
	}

	/**
	 * Called before the services are used but after {@link #provideConfig()}.
	 * @param config the config never null
	 */
	default void init(JStachioConfig config) {
	}

	/**
	 * Find the root service which is an aggregate of all found implementations.
	 * @return the root service never <code>null</code>.
	 */
	public static JStachioServices find() {
		return JStachioServicesResolver.INSTANCE;
	}

	/**
	 * Find all implementations minus the root aggregate.
	 * @return all custom implementations.
	 */
	public static Stream<JStachioServices> findAll() {
		return JStachioServicesResolver._services();
	}

	/**
	 * Finds a specific implementation using {@link Class#isAssignableFrom(Class)}.
	 * @param <T> the implementation type
	 * @param c the implementation type.
	 * @return an implementation if found
	 */
	public static <T extends JStachioServices> Optional<T> find(Class<T> c) {
		return findAll().filter(s -> c.isAssignableFrom(s.getClass())).map(c::cast).findFirst();
	}

}
