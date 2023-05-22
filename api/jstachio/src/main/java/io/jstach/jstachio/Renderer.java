package io.jstach.jstachio;

import java.io.IOException;

/**
 * Renders models of type {@code T} by writing to an Appendable. <em>Implementations
 * should be generally stateless and threadsafe.</em>
 *
 * @param <T> the model type
 */
public interface Renderer<T> {

	/**
	 * Renders the passed in model.
	 * @param <A> output type
	 * @param <E> error type
	 * @param model a model assumed never to be <code>null</code>.
	 * @param appendable the output to write to.
	 * @return the output passed in returned for convenience.
	 * @throws E if there is an error writing to the output
	 */
	public <A extends Output<E>, E extends Exception> A execute(T model, A appendable) throws E;

	/**
	 * Renders the passed in model.
	 * @param model a model assumed never to be <code>null</code>.
	 * @param appendable the appendable to write to.
	 * @throws IOException if there is an error writing to the appendable
	 */
	default void execute(T model, Appendable appendable) throws IOException {
		execute(model, Output.of(appendable));
	}

	/**
	 * A convenience method that does not throw {@link IOException} when using
	 * StringBuilder.
	 * @param model a model assumed never to be null.
	 * @param sb should never be null.
	 * @return the passed in {@link StringBuilder}.
	 */
	default StringBuilder execute(T model, StringBuilder sb) {
		return execute(model, Output.of(sb)).getBuffer();
	}

	/**
	 * Convenience method that directly renders the model as a String.
	 * @param model never null.
	 * @return the rendered model.
	 */
	default String execute(T model) {
		StringBuilder sb = new StringBuilder();
		return execute(model, sb).toString();
	}

}
