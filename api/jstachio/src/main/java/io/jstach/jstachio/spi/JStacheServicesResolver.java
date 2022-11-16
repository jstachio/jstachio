package io.jstach.jstachio.spi;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNull;

import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateInfo;

enum JStacheServicesResolver implements JStacheServices {

	INSTANCE;

	private static class Holder {

		private static Holder INSTANCE = Holder.of();

		private final List<JStacheServices> services;

		private final JStacheConfig config;

		private final JStacheFilter filter;

		private Holder(List<JStacheServices> services, JStacheConfig config, JStacheFilter filter) {
			super();
			this.services = services;
			this.config = config;
			this.filter = filter;
		}

		private static Holder of() {
			Iterable<JStacheServices> it = ServiceLoader.load(JStacheServices.class);
			List<JStacheServices> svs = new ArrayList<>();
			it.forEach(svs::add);
			List<JStacheConfig> configs = new ArrayList<>();
			List<JStacheFilter> filters = new ArrayList<>();

			for (var sv : svs) {
				var c = sv.provideConfig();
				if (c != null) {
					configs.add(c);
				}
			}
			JStacheConfig config = configs.isEmpty() ? SystemPropertyConfig.INSTANCE : new CompositeConfig(configs);
			for (var sv : svs) {
				sv.init(config);
				filters.add(sv.provideFilter());
			}
			JStacheFilter filter = new CompositeFilterChain(filters);
			return new Holder(List.copyOf(svs), config, filter);
		}

		JStacheConfig provideConfig() {
			return config;
		}

		JStacheFilter provideFilter() {
			return filter;
		}

	}

	static <T> Template<T> _renderer(Class<T> modelType) {
		return Renderers.getRenderer(modelType);
	}

	static Stream<JStacheServices> _services() {
		return Holder.INSTANCE.services.stream();
	}

	static JStacheConfig _config() {
		return Holder.INSTANCE.provideConfig();
	}

	static JStacheFilter _filter() {
		return Holder.INSTANCE.provideFilter();
	}

	@Override
	public @NonNull JStacheConfig provideConfig() {
		return _config();
	}

	@Override
	public @NonNull JStacheFilter provideFilter() {
		return _filter();
	}

	static TemplateInfo _templateInfo(Class<?> contextType) throws Exception {
		Exception error;
		try {
			Template<?> r = _renderer(contextType);
			return r;
		}
		catch (Exception e) {
			error = e;
		}
		var config = _config();

		if (!config.getBoolean(JStacheConfig.REFLECTION_TEMPLATE_DISABLE)) {
			Logger logger = config.getLogger(JStacheServices.class.getCanonicalName());
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING, "Could not find renderer for: " + contextType, error);
			}
			return TemplateInfos.templateOf(contextType);

		}
		throw error;

	}

}
