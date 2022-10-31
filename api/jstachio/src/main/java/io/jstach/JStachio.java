package io.jstach;

import java.io.IOException;

import io.jstach.spi.JStacheServices;

/**
 * User friendly utilities to help easily render models by using reflection to lookup
 * renderers.
 *
 * @see JStacheServices
 */
public enum JStachio {

	;

	public static <T> Renderer<T> renderer(Class<T> modelType) {
		return JStacheServices.renderer(modelType);
	}

	@SuppressWarnings("unchecked")
	public static void render(Object model, Appendable a) throws IOException {
		@SuppressWarnings("rawtypes")
		Renderer r = JStacheServices.renderer(model.getClass());
		r.render(model, a);
	}

	@SuppressWarnings("unchecked")
	public static StringBuilder render(Object model, StringBuilder a) {
		@SuppressWarnings("rawtypes")
		Renderer r = JStacheServices.renderer(model.getClass());
		return r.render(model, a);
	}

	@SuppressWarnings("unchecked")
	public static String render(Object model) {
		@SuppressWarnings("rawtypes")
		Renderer r = JStacheServices.renderer(model.getClass());
		return r.render(model);
	}

}
