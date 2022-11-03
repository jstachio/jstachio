package io.jstach;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

/**
 * A functional interface that has the model and template applied and now just needs an
 * {@link Appendable}.
 *
 * @author agentgt
 *
 */
@FunctionalInterface
public interface RenderFunction extends Consumer<Appendable> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	default void accept(Appendable t) {
		try {
			render(t);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}

	}

	/**
	 * Renders to the appendable.
	 * @param a appendable never <code>null</code>.
	 * @throws IOException if there is an error writing to the appendable.
	 */
	public void render(Appendable a) throws IOException;

	/**
	 * Renders as a String.
	 * @return the rendered string
	 */
	default String renderString() {
		return append(new StringBuilder()).toString();
	}

	/**
	 * Appends the rendering to a {@link StringBuilder}
	 * @param sb buffer never <code>null</code>.
	 * @return the passed in buffer never <code>null</code>
	 */
	default StringBuilder append(StringBuilder sb) {
		try {
			render(sb);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return sb;
	}

	/**
	 * Appends to the appendable.
	 * @param <A> the appendable type
	 * @param a the appendable never <code>null</code>.
	 * @return the same appendable passed in.
	 * @throws IOException if an error happens while writing to the appendable
	 */
	default <A extends Appendable> A append(A a) throws IOException {
		render(a);
		return a;
	}

	/**
	 * A marker method that the render function is broken and should not be used. This
	 * mainly for the filter pipeline.
	 * @return by default false
	 */
	default boolean isBroken() {
		return false;
	}

	/**
	 * A singleton broken render function.
	 * @author agentgt
	 *
	 */
	enum BrokenRenderFunction implements RenderFunction {

		/**
		 * singleton
		 */
		INSTANCE;

		/**
		 * {@inheritDoc} Will always throw an {@link IOException}.
		 */
		@Override
		public void render(Appendable a) throws IOException {
			throw new IOException();
		}

		/**
		 * {@inheritDoc} Is always true.
		 * @return true
		 */
		@Override
		public boolean isBroken() {
			return true;
		}

	}

}
