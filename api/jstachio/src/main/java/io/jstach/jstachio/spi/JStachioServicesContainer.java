package io.jstach.jstachio.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;

import org.eclipse.jdt.annotation.Nullable;

/**
 * A container that will hold all resolved {@link JStachioServices} and consolidate them
 * to a single one if needed.
 *
 * @author agentgt
 */
public final class JStachioServicesContainer {

	private final List<JStachioServices> services;

	private final JStachioConfig config;

	private final JStachioFilter filter;

	private final JStachioTemplateFinder templateFinder;

	private JStachioServicesContainer(List<JStachioServices> services, JStachioConfig config, JStachioFilter filter,
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
	public static JStachioServicesContainer of(Iterable<JStachioServices> it) {
		List<JStachioServices> svs = new ArrayList<>();
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
		return new JStachioServicesContainer(List.copyOf(svs), config, filter, templateFinder);
	}

	/**
	 * Composite Config
	 * @return config
	 */
	public JStachioConfig getConfig() {
		return config;
	}

	/**
	 * Composite Filter
	 * @return filter
	 */
	public JStachioFilter getFilter() {
		return filter;
	}

	/**
	 * Composite Template finder
	 * @return template finder
	 */
	public JStachioTemplateFinder getTemplateFinder() {
		return templateFinder;
	}

	/**
	 * Services
	 * @return found services
	 */
	public List<JStachioServices> getServices() {
		return services;
	}

}