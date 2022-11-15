package io.jstach;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.annotation.JStache;
import io.jstach.spi.JStacheFilter;
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
		filter(model).execute(model, a);
	}

	private static <T> Renderer<? super T> filter( //
			TemplateInfo template, //
			T context //
	) {
		@Nullable
		JStacheFilter filter = JStacheServices.find().provideFilter();
		if (filter == null) {
			throw new NullPointerException("Root service missing filter chain");
		}
		return (model, a) -> {
			filter.filter(template).process(model, a);
		};

	}

	private static <T> Renderer<? super T> filter(T context) {
		return filter(context, context.getClass());
	}

	private static <T> Renderer<? super T> filter(Object context, Class<? extends T> modelType) {
		return filter(templateInfo(modelType), context);
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
		return filter(model).execute(model, a);
	}

	/**
	 * Convenience method to render a model as a String.
	 * @param model the root context model. Never <code>null</code>.
	 * @return the rendered string.
	 */
	public static String render(Object model) {
		return filter(model).execute(model);
	}

}
