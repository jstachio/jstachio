package io.jstach;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Renders models of type {@code T} by writing to an Appendable. <em>Implementations
 * should be generally stateless and threadsafe.</em>
 *
 * @param <T> the model type
 */
@FunctionalInterface
public interface Renderer<T> {

	/**
	 * The generated renderers by default are suffix with this literal:
	 * <code>"Renderer"</code>
	 */
	public static final String IMPLEMENTATION_SUFFIX = "Renderer";

	/**
	 * Renders the passed in model.
	 * @param model a model assumed never to be <code>null</code>.
	 * @param appendable the appendable to write to.
	 * @throws IOException if there is an error writing to the appendable
	 */
	public void execute(T model, Appendable appendable) throws IOException;

	/**
	 * A convenience method that does not throw {@link IOException} when using
	 * StringBuilder.
	 * @param model a model assumed never to be null.
	 * @param sb should never be null.
	 * @return the passed in {@link StringBuilder}.
	 */
	default StringBuilder execute(T model, StringBuilder sb) {
		try {
			execute(model, (Appendable) sb);
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
	default String execute(T model) {
		StringBuilder sb = new StringBuilder();
		execute(model, sb);
		return sb.toString();
	}

	/**
	 * Applies the model to create a render function. Basically partial application
	 * functionally speaking.
	 * @param model the model the renderer uses to render.
	 * @return a function
	 */
	default RenderFunction apply(T model) {
		return a -> this.execute(model, a);
	}

}
