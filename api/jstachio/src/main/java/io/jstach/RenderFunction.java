package io.jstach;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

/**
 * A functional interface that essentially has the model and template applied and now just
 * needs an {@link Appendable}.
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

}
