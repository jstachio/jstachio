package io.jstach.jstachio.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

import org.eclipse.jdt.annotation.Nullable;

/**
 * A container that will hold all resolved {@link JStachioExtension}s and consolidate them
 * to a single instances of various services.
 *
 * @apiNote While this interface looks similar to {@link JStachioExtension} it is not an
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
	 * mechanism like the {@link ServiceLoader} or a DI framework. <em>The order of the
	 * extensions is important and primacy order takes precedence!</em>
	 * @param extensions found extensions.
	 * @return bean like container of services.
	 */
	public static JStachioExtensions of(Iterable<JStachioExtension> extensions) {
		return DefaultJStachioExtensions.of(extensions);
	}

	/**
	 * Resolves extensions from the {@link ServiceLoader} with {@link JStachioExtension}
	 * as the SPI.
	 * @return jstachio extensions found by the ServiceLoader
	 */
	public static JStachioExtensions of() {
		Iterable<JStachioExtension> it = ServiceLoader.load(JStachioExtension.class);
		return of(it);
	}

	/**
	 * Composite Config where the first config that returns a nonnull for
	 * {@link JStachioConfig#getProperty(String)} is used.
	 * @return config
	 */
	JStachioConfig getConfig();

	/**
	 * Composite Filter where the ordering of the filter is based on a combination of
	 * {@link JStachioFilter#order()} first and then the order in the iterable passed to
	 * {@link #of(Iterable)}.
	 * @return filter
	 */
	JStachioFilter getFilter();

	/**
	 * Composite Template finder where the first template finder that finds a template is
	 * used.
	 * @return template finder
	 */
	JStachioTemplateFinder getTemplateFinder();

	/**
	 * The orignal contained extensions excluding the composites.
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
			JStachioTemplateFinder templateFinder) {
		super();
		this.services = services;
		this.config = config;
		this.filter = filter;
		this.templateFinder = templateFinder;
	}

	/**
	 * Create a container from service providers.
	 * @param it services
	 * @return bean like container of services.
	 */
	static JStachioExtensions of(Iterable<JStachioExtension> it) {
		List<JStachioExtensionProvider> svs = new ArrayList<>();
		it.forEach(s -> svs.add(JStachioExtensionProvider.of(s)));

		List<JStachioConfig> configs = new ArrayList<>();
		List<JStachioFilter> filters = new ArrayList<>();
		List<JStachioTemplateFinder> finders = new ArrayList<>();

		for (var sv : svs) {
			var c = sv.provideConfig();
			if (c != null) {
				configs.add(c);
			}
		}
		JStachioConfig config = configs.isEmpty() ? SystemPropertyConfig.INSTANCE : new CompositeConfig(configs);

		for (var sv : svs) {
			sv.init(config);
			@Nullable
			JStachioFilter filt = sv.provideFilter();
			if (filt != null) {
				filters.add(filt);
			}
			@Nullable
			JStachioTemplateFinder find = sv.provideTemplateFinder();
			if (find != null) {
				finders.add(find);
			}
		}
		JStachioFilter filter = JStachioFilter.compose(filters);
		if (finders.isEmpty() && !config.getBoolean(JStachioConfig.REFLECTION_TEMPLATE_DISABLE, false)) {
			finders.add(
					JStachioTemplateFinder.cachedTemplateFinder(JStachioTemplateFinder.defaultTemplateFinder(config)));
		}
		JStachioTemplateFinder templateFinder = CompositeTemplateFinder.of(finders);
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