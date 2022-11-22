package io.jstach.jstachio.spi;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.TemplateInfo;

enum JStachioServicesResolver implements JStachioServices {

	INSTANCE;

	private static class Holder implements JStachio {

		private static Holder INSTANCE = Holder.of();

		private final List<JStachioServices> services;

		private final JStachioConfig config;

		private final JStachioFilter filter;

		private final JStachioTemplateFinder templateFinder;

		private final JStachio jstachio;

		private Holder( //
				List<JStachioServices> services, //
				JStachioConfig config, //
				JStachioFilter filter, //
				@Nullable JStachioTemplateFinder templateFinder, //
				@Nullable JStachio jstachio) {
			super();
			this.services = services;
			this.config = config;
			this.filter = filter;
			this.templateFinder = templateFinder != null ? templateFinder : new DefaultTemplateFinder(config);
			this.jstachio = jstachio != null ? jstachio : this;
		}

		private static Holder of() {
			Iterable<JStachioServices> it = ServiceLoader.load(JStachioServices.class);
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
			JStachio jstachio = null;
			@Nullable
			JStachioTemplateFinder templateFinder = null;

			// TODO if we find a non default JStachio should we even bother loading other
			// extensions?
			for (var sv : svs) {
				sv.init(config);
				filters.add(sv.provideFilter());
				var finder = sv.provideTemplateFinder();
				if (templateFinder != null && finder != null) {
					throw new ServiceConfigurationError("Multiple template finders found by service loader. first = "
							+ templateFinder.getClass().getName() + ", second = " + finder.getClass().getName());
				}
				templateFinder = finder;
				var js = sv.provideJStachio();
				if (jstachio != null && js != null) {
					throw new ServiceConfigurationError("Multiple JStachios found by service loader. first = "
							+ jstachio.getClass().getName() + ", second = " + js.getClass().getName());
				}
				jstachio = js;
			}

			JStachioFilter filter = new CompositeFilterChain(filters);
			return new Holder(List.copyOf(svs), config, filter, templateFinder, jstachio);
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
			try {
				return templateFinder.findTemplate(modelType);
			}
			catch (Exception e) {
				sneakyThrow0(e);
				throw new RuntimeException(e);
			}
		}

		JStachioConfig config() {
			return config;
		}

		JStachioFilter filter() {
			return filter;
		}

		JStachio jstachio() {
			return jstachio;
		}

		JStachioTemplateFinder templateFinder() {
			return templateFinder;
		}

	}

	static Stream<JStachioServices> _services() {
		return Holder.INSTANCE.services.stream();
	}

	static JStachioConfig _config() {
		return Holder.INSTANCE.config();
	}

	@Override
	public @Nullable JStachioTemplateFinder provideTemplateFinder() {
		return Holder.INSTANCE.templateFinder();
	}

	@Override
	public @NonNull JStachioConfig provideConfig() {
		return _config();
	}

	@Override
	public @NonNull JStachioFilter provideFilter() {
		return Holder.INSTANCE.filter();
	}

	@Override
	public @NonNull JStachio provideJStachio() {
		return Holder.INSTANCE.jstachio();
	}

}
