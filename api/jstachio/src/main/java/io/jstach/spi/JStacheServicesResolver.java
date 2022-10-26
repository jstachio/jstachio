package io.jstach.spi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import io.jstach.Formatter;
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

	// private ClassValue<Renderer<?>> rendererCache = new ClassValue<Renderer<?>>() {
	// @Override
	// protected @Nullable Renderer<?> computeValue(@Nullable Class<?> type) {
	// return _findRenderer(type);
	// }
	// };

	// @Override
	// public <T> Optional<Renderer<T>> findRenderer(Class<T> modelType) {
	// @SuppressWarnings("unchecked")
	// Renderer<T> r = (Renderer<T>) rendererCache.get(modelType);
	// if (r == null) {
	// return Optional.empty();
	// }
	// return Optional.of(r);
	// }

	static <T> Renderer<T> _renderer(Class<T> modelType) {
		return Renderers.getRenderer(modelType);
	}

	static Stream<JStacheServices> _services() {
		return Holder.INSTANCE.services.stream();
	}

	@Override
	public RenderFunction renderer(TemplateInfo template, Object context, RenderFunction previous) throws IOException {
		RenderFunction current = previous;
		for (var rs : Holder.INSTANCE.services) {
			current = rs.renderer(template, context, current);
		}
		return current;
	}

	// @Override
	// public List<Renderer<?>> findRenderers() {
	// List<Renderer<?>> list = new ArrayList<>();
	// for (var rs : Holder.INSTANCE.services) {
	// list.addAll(rs.findRenderers());
	// }
	// ServiceLoader.load(Renderer.class).stream().forEach(p -> {
	// try {
	// list.add(p.get());
	// } catch (ServiceConfigurationError e) {
	// }
	// });
	// return list;
	// }

	@Override
	public Formatter formatter(Formatter formatter) {
		Formatter current = formatter;
		for (var rs : Holder.INSTANCE.services) {
			current = rs.formatter(current);
		}
		return current;
	}

}
