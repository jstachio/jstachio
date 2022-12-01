package io.jstach.jstachio.spi;

import java.util.ServiceLoader;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstachio.JStachio;

enum JStachioServicesResolver implements JStachioServices {

	INSTANCE;

	private static class Holder extends AbstractJStachio implements JStachio {

		private static Holder INSTANCE = Holder.of();

		private final JStachioServicesContainer container;

		public Holder(JStachioServicesContainer container) {
			super();
			this.container = container;
		}

		private static Holder of() {
			Iterable<JStachioServices> it = ServiceLoader.load(JStachioServices.class);
			return new Holder(JStachioServicesContainer.of(it));
		}

		@Override
		protected JStachioTemplateFinder templateFinder() {
			return container.getTemplateFinder();
		}

		@Override
		protected JStachioFilter filter() {
			return container.getFilter();
		}

	}

	static Stream<JStachioServices> _services() {
		return Holder.INSTANCE.container.getServices().stream();
	}

	static JStachioConfig _config() {
		return Holder.INSTANCE.container.getConfig();
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

	public JStachio provideJStachio() {
		return Holder.INSTANCE;
	}

}
