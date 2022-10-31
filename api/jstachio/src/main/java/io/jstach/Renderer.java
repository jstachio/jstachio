package io.jstach;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Renders models of type {@code T} by writing to an Appendable. <em>Implementations
 * should be generally stateless and threadsafe.</em>
 *
 * @param <T> the model type
 */
public interface Renderer<T> {

	/**
	 * Renders the passed in model.
	 * @param model a model assumed never to be null.
	 * @param appendable the appendable to write to.
	 * @throws IOException
	 */
	public void render(T model, Appendable appendable) throws IOException;

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
	 * Checks to see if a renderer supports the model class.
	 * @param type the class of the model.
	 * @return if this renderer supports the class.
	 */
	public boolean supportsType(Class<?> type);

}
