package io.jstach.jstachio;

import java.io.IOException;

public interface Output<E extends Exception> {

	/**
	 * Analogous to {@link Appendable#append(CharSequence)}.
	 * @param a appendable to write to. Always non null.
	 * @param s unlike appendable always non null.
	 * @throws E if an error happens while writting to the appendable
	 * @apiNote Implementations are required to implement this method.
	 */
	public void append(CharSequence s) throws E;

	/**
	 * Analogous to {@link Appendable#append(CharSequence, int, int)}.
	 * @param a appendable to write to. Never null.
	 * @param csq Unlike appendable never null.
	 * @param start start inclusive
	 * @param end end exclusive
	 * @throws E if an error happens while writting to the appendable
	 * @apiNote Implementations are required to implement this method.
	 */
	public void append(CharSequence csq, int start, int end) throws E;

	/**
	 * Appends a character to an appendable.
	 * @param a appendable to write to. Never null.
	 * @param c character
	 * @throws IOException if an error happens while writting to the appendable
	 * @apiNote Implementations are required to implement this method.
	 */
	public void append(char c) throws E;

	/**
	 * Write a short by using {@link String#valueOf(int)}
	 * @param a appendable to write to. Never null.
	 * @param s short
	 * @throws IOException if an error happens while writting to the appendable
	 */
	default void append(short s) throws E {
		append(String.valueOf(s));
	}

	/**
	 * Write a int by using {@link String#valueOf(int)}.
	 * <p>
	 * Implementations should override if they want different behavior or able to support
	 * appendables that can write the native type.
	 * @param a appendable to write to. Never null.
	 * @param i int
	 * @throws E if an error happens while writting to the appendable
	 */
	default void append(int i) throws E {
		append(String.valueOf(i));
	}

	/**
	 * Write a long by using {@link String#valueOf(long)}.
	 * <p>
	 * Implementations should override if they want different behavior or able to support
	 * appendables that can write the native type.
	 * @param a appendable to write to. Never null.
	 * @param l long
	 * @throws E if an error happens while writting to the appendable
	 */
	default void append(long l) throws E {
		append(String.valueOf(l));
	}

	/**
	 * Write a long by using {@link String#valueOf(long)}.
	 * <p>
	 * Implementations should override if they want different behavior or able to support
	 * appendables that can write the native type.
	 * @param a appendable to write to. Never null.
	 * @param d double
	 * @throws E if an error happens while writting to the appendable
	 */
	default void append(double d) throws E {
		append(String.valueOf(d));
	}

	/**
	 * Write a long by using {@link String#valueOf(long)}.
	 * <p>
	 * Implementations should override if they want different behavior or able to support
	 * appendables that can write the native type.
	 * @param a appendable to write to. Never null.
	 * @param b boolean
	 * @throws E if an error happens while writting to the appendable
	 */
	default void append(boolean b) throws E {
		append(String.valueOf(b));
	}

	public static Output<IOException> of(Appendable a) {
		return new AppendableOutput(a);
	}

	public class StringOutput implements Output<RuntimeException> {

		private final StringBuilder buffer;

		public StringOutput(StringBuilder buffer) {
			super();
			this.buffer = buffer;
		}

		@Override
		public void append(CharSequence s) {
			buffer.append(s);
		}

		@Override
		public void append(CharSequence csq, int start, int end) {
			buffer.append(csq, start, end);

		}

		@Override
		public void append(char c) {
			buffer.append(c);

		}

		@Override
		public String toString() {
			return buffer.toString();
		}

	}

}

class AppendableOutput implements Output<IOException> {

	private final Appendable appendable;

	public AppendableOutput(Appendable appendable) {
		super();
		this.appendable = appendable;
	}

	@Override
	public void append(CharSequence s) throws IOException {
		appendable.append(s);
	}

	@Override
	public void append(CharSequence csq, int start, int end) throws IOException {
		appendable.append(csq, start, end);

	}

	@Override
	public void append(char c) throws IOException {
		appendable.append(c);

	}

}
