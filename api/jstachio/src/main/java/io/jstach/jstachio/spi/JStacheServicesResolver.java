package io.jstach.jstachio.spi;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateInfo;

enum JStacheServicesResolver implements JStacheServices {

	INSTANCE;

	private static class Holder implements JStachio {

		private static Holder INSTANCE = Holder.of();

		private final List<JStacheServices> services;

		private final JStacheConfig config;

		private final JStacheFilter filter;

		private final JStachio jstachio;

		private Holder(List<JStacheServices> services, JStacheConfig config, JStacheFilter filter,
				@Nullable JStachio jstachio) {
			super();
			this.services = services;
			this.config = config;
			this.filter = filter;
			this.jstachio = jstachio == null ? this : jstachio;
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
			@Nullable
			JStachio jstachio = null;

			for (var sv : svs) {
				sv.init(config);
				filters.add(sv.provideFilter());
				var js = sv.provideJStachio();
				if (jstachio != null && js != null) {
					throw new ServiceConfigurationError("Multiple JStachios found by service loader. first = "
							+ jstachio.getClass().getName() + ", second = " + js.getClass().getName());
				}
				jstachio = js;
			}

			JStacheFilter filter = new CompositeFilterChain(filters);
			return new Holder(List.copyOf(svs), config, filter, jstachio);
		}

		@Override
		public void execute(Object model, Appendable appendable) throws IOException {
			TemplateInfo template = template(model.getClass());
			filter.filter(template).process(model, appendable);
		}

		@Override
		public String execute(Object model) {
			return execute(model, new StringBuilder()).toString();
		}

		@Override
		public StringBuilder execute(Object model, StringBuilder sb) {
			try {
				execute(model, (Appendable) sb);
				return sb;
			}
			catch (IOException e) {
				sneakyThrow0(e);
				throw new UncheckedIOException(e);
			}
		}

		@SuppressWarnings("unchecked")
		static <E extends Throwable> void sneakyThrow0(final Throwable x) throws E {
			throw (E) x;
		}

		protected TemplateInfo template(Class<?> modelType) throws IOException {
			TemplateInfo template;
			try {
				template = _templateInfo(modelType);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			if (template == null) {
				throw new RuntimeException("template not found for modelType: " + modelType);
			}
			return template;
		}

		JStacheConfig config() {
			return config;
		}

		JStacheFilter filter() {
			return filter;
		}

		JStachio jstachio() {
			return jstachio;
		}

	}

	static <T> Template<T> _renderer(Class<T> modelType) {
		return Renderers.getRenderer(modelType);
	}

	static Stream<JStacheServices> _services() {
		return Holder.INSTANCE.services.stream();
	}

	static JStacheConfig _config() {
		return Holder.INSTANCE.config();
	}

	static JStacheFilter _filter() {
		return Holder.INSTANCE.filter();
	}

	@Override
	public @NonNull JStacheConfig provideConfig() {
		return _config();
	}

	@Override
	public @NonNull JStacheFilter provideFilter() {
		return _filter();
	}

	@Override
	public @NonNull JStachio provideJStachio() {
		return Holder.INSTANCE.jstachio();
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
