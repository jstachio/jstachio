package io.jstach.spi;

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

		private Holder(List<JStacheServices> services) {
			super();
			this.services = services;
		}

		private static Holder of() {
			Iterable<JStacheServices> it = ServiceLoader.load(JStacheServices.class);
			List<JStacheServices> svs = new ArrayList<>();
			it.forEach(svs::add);
			return new Holder(List.copyOf(svs));
		}

	}

	static <T> Renderer<T> _renderer(Class<T> modelType) {
		return Renderers.getRenderer(modelType);
	}

	static Stream<JStacheServices> _services() {
		return Holder.INSTANCE.services.stream();
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
