package io.jstach;

import java.io.IOException;

import io.jstach.annotation.JStache;
import io.jstach.spi.JStacheServices;

/**
 * Render models by using reflection to lookup renderers as well as apply filtering and
 * fallback mechanisms.
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
	public static void render(Object model, Appendable a) throws IOException {
		filter(model).append(a);
	}

	/**
	 * Applies filtering to a Renderer.
	 * @param <T> declared type of the context
	 * @param context an instance of the context
	 * @param renderer to filter
	 * @return the filtered rendering function
	 */
	public static <T> RenderFunction filter(T context, Renderer<T> renderer) {
		var rf = JStacheServices.findService().filter(renderer, context, renderer.apply(context));
		return rf;
	}

	private static <T> RenderFunction filter(Object context) {
		return filter(context, context.getClass());
	}

	@SuppressWarnings("unchecked")
	private static <T> RenderFunction filter(Object context, Class<?> modelType) {
		@SuppressWarnings("rawtypes")
		Renderer r = renderer(modelType);
		return filter(context, r);

	}

	/**
	 * Finds a render by using the models class and then render the model by writting to
	 * the passed StringBuilder.
	 * @param model never <code>null</code>
	 * @param a appendable never <code>null</code>
	 * @return the passed in {@link StringBuilder}
	 */
	public static StringBuilder render(Object model, StringBuilder a) {
		return filter(model).append(a);
	}

	/**
	 * Convenience method to render a model as a String.
	 * @param model the root context model. Never <code>null</code>.
	 * @return the rendered string.
	 */
	public static String render(Object model) {
		return filter(model).renderString();
	}

}
