package io.jstach;

import java.io.IOException;

import io.jstach.annotation.JStache;
import io.jstach.spi.JStacheServices;

/**
 * User friendly utilities to help easily render models by using reflection to lookup
 * renderers.
 *
 * @see JStacheServices
 */
public enum JStachio {

	;

	/**
	 * Finds a renderer for a model class.
	 * @param <T> the type of model.
	 * @param modelType the class of the model (annotated with {@link JStache})
	 * @return renderer for the specifi type.
	 * @throws RuntimeException if the renderer is not found for the type.
	 */
	public static <T> Renderer<T> renderer(Class<T> modelType) {
		return JStacheServices.renderer(modelType);
	}

	/**
	 * Finds a render by using the models class and then render the model by writting to
	 * the appendable.
	 * @param model never <code>null</code>
	 * @param a appendable never <code>null</code>
	 * @throws IOException if there is an error using the appendable
	 */
	@SuppressWarnings("unchecked")
	public static void render(Object model, Appendable a) throws IOException {
		@SuppressWarnings("rawtypes")
		Renderer r = JStacheServices.renderer(model.getClass());
		r.render(model, a);
	}

	/**
	 * Finds a render by using the models class and then render the model by writting to
	 * the passed StringBuilder.
	 * @param model never <code>null</code>
	 * @param a appendable never <code>null</code>
	 * @return the passed in {@link StringBuilder}
	 */
	@SuppressWarnings("unchecked")
	public static StringBuilder render(Object model, StringBuilder a) {
		@SuppressWarnings("rawtypes")
		Renderer r = JStacheServices.renderer(model.getClass());
		return r.render(model, a);
	}

	/**
	 * Convenience method to render a model as a String.
	 * @param model the root context model. Never <code>null</code>.
	 * @return the rendered string.
	 */
	@SuppressWarnings("unchecked")
	public static String render(Object model) {
		@SuppressWarnings("rawtypes")
		Renderer r = JStacheServices.renderer(model.getClass());
		return r.render(model);
	}

}
