package io.jstach;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Renders models of type {@code T} by writing to an Appendable. <em>Implementations
 * should be generally stateless and threadsafe.</em>
 *
 * @param <T> the model type
 */
public interface Renderer<T> extends TemplateInfo {

	/**
	 * Renders the passed in model.
	 * @param model a model assumed never to be <code>null</code>.
	 * @param appendable the appendable to write to.
	 * @throws IOException if there is an error writing to the appendable
	 */
	default void render(T model, Appendable appendable) throws IOException {
		render(model, appendable, Formatter.of(templateFormatter()), Escaper.of(templateEscaper()));
	}

	/**
	 * Renders the passed in model.
	 * @param model a model assumed never to be <code>null</code>.
	 * @param a appendable to write to.
	 * @param formatter formats variables before they are passed to the escaper
	 * @param escaper used to write escaped variables
	 * @throws IOException if an error occurs while writing to the appendable
	 */
	public void render(T model, //
			Appendable a, //
			Formatter formatter, //
			Escaper escaper) throws IOException;

	/**
	 * A convenience method that does not throw {@link IOException} when using
	 * StringBuilder.
	 * @param model a model assumed never to be null.
	 * @param sb should never be null.
	 * @return the passed in {@link StringBuilder}.
	 */
	default StringBuilder render(T model, StringBuilder sb) {
		try {
			render(model, (Appendable) sb);
			return sb;
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Convenience method that directly renders the model as a String.
	 * @param model never null.
	 * @return the rendered model.
	 */
	default String render(T model) {
		StringBuilder sb = new StringBuilder();
		render(model, sb);
		return sb.toString();
	}

	/**
	 * Applies the model to create a render function. Basically partial application
	 * functionally speaking.
	 * @param model the model the renderer uses to render.
	 * @return a function
	 */
	default RenderFunction apply(T model) {
		return a -> this.render(model, a);
	}

	/**
	 * Checks to see if a renderer supports the model class.
	 * @param type the class of the model.
	 * @return if this renderer supports the class.
	 */
	public boolean supportsType(Class<?> type);

}
