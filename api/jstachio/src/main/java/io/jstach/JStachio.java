package io.jstach;

import java.io.IOException;

import io.jstach.annotation.JStache;
import io.jstach.spi.JStacheServices;

/**
 * Render models by using reflection to lookup renderers as well as apply filtering and
 * fallback mechanisms.
 *
 * @see JStacheServices
 * @see JStache
 */
public final class JStachio {

	private JStachio() {
	}

	/**
	 * Finds a renderer for a model class.
	 * @apiNote The returned raw renderer does not have any filtering applied.
	 * @param <T> the type of model.
	 * @param modelType the class of the model (annotated with {@link JStache})
	 * @return renderer for the specifi type.
	 * @throws RuntimeException if the renderer is not found for the type.
	 */
	public static <T> Renderer<T> renderer(Class<T> modelType) {
		return JStacheServices.renderer(modelType);
	}

	/**
	 * Finds a render by using the models class if possible and then applies filtering and
	 * then finally render the model by writting to the appendable.
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
		return JStacheServices.find().filter(renderer, context, renderer.apply(context));
	}

	/**
	 * Applies filtering to a template.
	 * @param context an instance of the context
	 * @param template to filter
	 * @return the filtered rendering function
	 */
	@SuppressWarnings("unchecked")
	private static RenderFunction filter(Object context, TemplateInfo template) {
		RenderFunction rf;
		if (template instanceof @SuppressWarnings("rawtypes") Renderer renderer) {
			rf = JStacheServices.find().filter(renderer, context, renderer.apply(context));
		}
		else {
			rf = JStacheServices.find().filter(template, context, RenderFunction.BrokenRenderFunction.INSTANCE);
		}
		return rf;
	}

	private static <T> RenderFunction filter(Object context) {
		return filter(context, context.getClass());
	}

	private static <T> RenderFunction filter(Object context, Class<?> modelType) {
		return filter(context, templateInfo(modelType));
	}

	private static TemplateInfo templateInfo(Class<?> modelType) {
		TemplateInfo template;
		try {
			template = JStacheServices.templateInfo(modelType);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (template == null) {
			throw new RuntimeException("template not found for modelType: " + modelType);
		}
		return template;

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
