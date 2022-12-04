package io.jstach.jstachio.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.eclipse.jdt.annotation.Nullable;

/**
 * A container that will hold all resolved {@link JStachioExtension}s and consolidate them
 * to a single instances of various services.
 *
 * @apiNote While this interface looks similar {@link JStachioExtension} it is not an
 * extension but rather an immutable bean like container. The methods are purposely java
 * bean style (which is not the default in JStachio as JStachio prefers newer record like
 * accessor method names) to support as many frameworks as possible.
 * @author agentgt
 */
public interface JStachioExtensions {

	/**
	 * A marker interface used for JStachio implementations that provide access to
	 * extensions.
	 *
	 * @author agentgt
	 *
	 */
	public interface Provider {

		/**
		 * The available extensions.
		 * @return The avaiable resolved extensions.
		 */
		public JStachioExtensions extensions();

	}

	/**
	 * Resolve from an iterable of extensions that usually come from some discovery
	 * mechanism like the {@link ServiceLoader} or a DI framework.
	 * @param extensions found extensions.
	 * @return bean like container of services.
	 */
	public static JStachioExtensions of(Iterable<JStachioExtension> extensions) {
		return DefaultJStachioExtensions.of(extensions);
	}

	/**
	 * Composite Config
	 * @return config
	 */
	JStachioConfig getConfig();

	/**
	 * Composite Filter
	 * @return filter
	 */
	JStachioFilter getFilter();

	/**
	 * Composite Template finder
	 * @return template finder
	 */
	JStachioTemplateFinder getTemplateFinder();

	/**
	 * Services
	 * @return found services
	 */
	List<JStachioExtension> getExtensions();

	/**
	 * Finds a specific implementation using {@link Class#isAssignableFrom(Class)}.
	 * @param <T> the implementation type
	 * @param c the implementation type.
	 * @return an implementation if found
	 */
	default <T extends JStachioExtension> Optional<T> findExtension(Class<T> c) {
		return getExtensions().stream().filter(s -> c.isAssignableFrom(s.getClass())).map(c::cast).findFirst();
	}

}

class DefaultJStachioExtensions implements JStachioExtensions {

	private final List<JStachioExtension> services;

	private final JStachioConfig config;

	private final JStachioFilter filter;

	private final JStachioTemplateFinder templateFinder;

	private DefaultJStachioExtensions(List<JStachioExtension> services, JStachioConfig config, JStachioFilter filter,
			@Nullable JStachioTemplateFinder templateFinder) {
		super();
		this.services = services;
		this.config = config;
		this.filter = filter;
		this.templateFinder = templateFinder != null ? templateFinder : new DefaultTemplateFinder(config);
		;
	}

	/**
	 * Create a container from service providers.
	 * @param it services
	 * @return bean like container of services.
	 */
	static JStachioExtensions of(Iterable<JStachioExtension> it) {
		List<JStachioExtension> svs = new ArrayList<>();
		it.forEach(svs::add);
		List<JStachioConfig> configs = new ArrayList<>();
		List<JStachioFilter> filters = new ArrayList<>();

		for (var sv : svs) {
			var c = sv.provideConfig();
			if (c != null) {
				configs.add(c);
			}
		}
		JStachioConfig config = configs.isEmpty() ? SystemPropertyConfig.INSTANCE : new CompositeConfig(configs);
		@Nullable
		JStachioTemplateFinder templateFinder = null;

		for (var sv : svs) {
			sv.init(config);
			@Nullable
			JStachioFilter f = sv.provideFilter();
			if (f != null) {
				filters.add(f);
			}
			var finder = sv.provideTemplateFinder();
			if (templateFinder != null && finder != null) {
				throw new ServiceConfigurationError("Multiple template finders found by service loader. first = "
						+ templateFinder.getClass().getName() + ", second = " + finder.getClass().getName());
			}
			templateFinder = finder;
		}
		JStachioFilter filter = JStachioFilter.compose(filters);
		return new DefaultJStachioExtensions(List.copyOf(svs), config, filter, templateFinder);
	}

	/**
	 * Composite Config
	 * @return config
	 */
	@Override
	public JStachioConfig getConfig() {
		return config;
	}

	/**
	 * Composite Filter
	 * @return filter
	 */
	@Override
	public JStachioFilter getFilter() {
		return filter;
	}

	/**
	 * Composite Template finder
	 * @return template finder
	 */
	@Override
	public JStachioTemplateFinder getTemplateFinder() {
		return templateFinder;
	}

	/**
	 * Services
	 * @return found services
	 */
	@Override
	public List<JStachioExtension> getExtensions() {
		return services;
	}

}