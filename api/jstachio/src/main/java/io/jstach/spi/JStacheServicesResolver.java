package io.jstach.spi;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import io.jstach.RenderFunction;
import io.jstach.Renderer;
import io.jstach.TemplateInfo;

enum JStacheServicesResolver implements JStacheServices {

	INSTANCE;

	private static class Holder {

		private static Holder INSTANCE = Holder.of();

		private final List<JStacheServices> services;

		private final JStacheConfig config;

		private Holder(List<JStacheServices> services, JStacheConfig config) {
			super();
			this.services = services;
			this.config = config;
		}

		private static Holder of() {
			Iterable<JStacheServices> it = ServiceLoader.load(JStacheServices.class);
			List<JStacheServices> svs = new ArrayList<>();
			it.forEach(svs::add);
			List<JStacheConfig> configs = new ArrayList<>();
			for (var sv : svs) {
				var c = sv.provideConfig();
				if (c != null) {
					configs.add(c);
				}
			}
			JStacheConfig config = configs.isEmpty() ? SystemPropertyConfig.INSTANCE : new CompositeConfig(configs);
			for (var sv : svs) {
				sv.init(config);
			}
			return new Holder(List.copyOf(svs), config);
		}

		JStacheConfig getConfig() {
			return config;
		}

	}

	static <T> Renderer<T> _renderer(Class<T> modelType) {
		return Renderers.getRenderer(modelType);
	}

	static Stream<JStacheServices> _services() {
		return Holder.INSTANCE.services.stream();
	}

	static JStacheConfig _config() {
		return Holder.INSTANCE.getConfig();
	}

	static TemplateInfo _templateInfo(Class<?> contextType) throws Exception {
		Exception error;
		try {
			Renderer<?> r = _renderer(contextType);
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

	@Override
	public RenderFunction filter(TemplateInfo template, Object context, RenderFunction previous) {
		RenderFunction current = previous;
		for (var rs : Holder.INSTANCE.services) {
			current = rs.filter(template, context, current);
		}
		return current;
	}

}
