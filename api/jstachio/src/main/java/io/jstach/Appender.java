package io.jstach;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * A singleton like decorator for appendables that has additional methods for dealing with
 * native types.
 *
 * @apiNote Unlike an Appendable this class is expected to be reused so avoid state.
 * @author agentgt
 * @param <A> the appendable
 * @see Escaper
 */
public interface Appender<A extends Appendable> {

	/**
	 * Analogous to {@link Appendable#append(CharSequence)}.
	 * @param a appendable to write to. Always non null.
	 * @param s unlike appendable always non null.
	 * @throws IOException
	 */
	public void append(A a, CharSequence s) throws IOException;

	/**
	 * Analogous to {@link Appendable#append(CharSequence, int, int)}.
	 * @param a appendable to write to. Never null.
	 * @param csq Unlike appendable never null.
	 * @param start start inclusive
	 * @param end end exclusive
	 * @throws IOException
	 */
	public void append(A a, CharSequence csq, int start, int end) throws IOException;

	/**
	 * Appends a character to an appendable.
	 * @param a appendable to write to. Never null.
	 * @param c character
	 * @throws IOException
	 */
	public void append(A a, char c) throws IOException;

	/**
	 * Write a short by using {@link String#valueOf(int)}
	 * @param a appendable to write to. Never null.
	 * @param s short
	 * @throws IOException
	 */
	default void append(A a, short s) throws IOException {
		append(a, String.valueOf(s));
	}

	/**
	 * Write a int by using {@link String#valueOf(int)}.
	 * <p>
	 * Implementations should override if they want different behavior or able to support
	 * appendables that can write the native type.
	 * @param a appendable to write to. Never null.
	 * @param i int
	 * @throws IOException
	 */
	default void append(A a, int i) throws IOException {
		append(a, String.valueOf(i));
	}

	/**
	 * Write a long by using {@link String#valueOf(long)}.
	 * <p>
	 * Implementations should override if they want different behavior or able to support
	 * appendables that can write the native type.
	 * @param a appendable to write to. Never null.
	 * @param l long
	 * @throws IOException
	 */
	default void append(A a, long l) throws IOException {
		append(a, String.valueOf(l));
	}

	/**
	 * Write a long by using {@link String#valueOf(long)}.
	 * <p>
	 * Implementations should override if they want different behavior or able to support
	 * appendables that can write the native type.
	 * @param a appendable to write to. Never null.
	 * @param d double
	 * @throws IOException
	 */
	default void append(A a, double d) throws IOException {
		append(a, String.valueOf(d));
	}

	/**
	 * Write a long by using {@link String#valueOf(long)}.
	 * <p>
	 * Implementations should override if they want different behavior or able to support
	 * appendables that can write the native type.
	 * @param a appendable to write to. Never null.
	 * @param b boolean
	 * @throws IOException
	 */
	default void append(A a, boolean b) throws IOException {
		append(a, String.valueOf(b));
	}

	/**
	 * Decorates an appendable with this appender.
	 * @param appendable never null.
	 * @return Appendable never null.
	 */
	default Appendable toAppendable(A appendable) {
		return new AppenderAppendable<>(this, appendable);
	}

	enum DefaultAppender implements Appender<Appendable> {

		INSTANCE;

		@Override
		public void append(Appendable a, CharSequence s) throws IOException {
			a.append(s);
		}

		@Override
		public void append(Appendable a, CharSequence csq, int start, int end) throws IOException {
			a.append(csq, start, end);
		}

		@Override
		public void append(Appendable a, char c) throws IOException {
			a.append(c);
		}

	}

	/**
	 * An appender that will directly call StringBuilder methods for native types.
	 * <p>
	 * This is a low level utility class for where performance matters.
	 *
	 * @author agentgt
	 *
	 */
	enum StringAppender implements Appender<StringBuilder> {

		INSTANCE;

		@Override
		public void append(StringBuilder a, CharSequence s) throws IOException {
			a.append(s);
		}

		@Override
		public void append(StringBuilder a, CharSequence csq, int start, int end) throws IOException {
			a.append(csq, start, end);
		}

		@Override
		public void append(StringBuilder a, char c) throws IOException {
			a.append(c);
		}

		@Override
		public void append(StringBuilder a, short s) throws IOException {
			a.append(s);
		}

		public void append(StringBuilder a, int i) throws IOException {
			a.append(i);
		}

		public void append(StringBuilder a, long l) throws IOException {
			a.append(l);
		}

		public void append(StringBuilder a, double d) throws IOException {
			a.append(d);
		}

		public void append(StringBuilder a, boolean b) throws IOException {
			a.append(b);
		}

	}

	class AppenderAppendable<A extends Appendable> implements Appendable {

		private final Appender<A> appender;

		private final A appendable;

		public AppenderAppendable(Appender<A> appender, A appendable) {
			super();
			this.appender = appender;
			this.appendable = appendable;
		}

		@Override
		public @NonNull Appendable append(@Nullable CharSequence csq) throws @Nullable IOException {
			appender.append(appendable, csq);
			return this;
		}

		@Override
		public @NonNull Appendable append(@Nullable CharSequence csq, int start, int end) throws IOException {
			appender.append(appendable, csq, start, end);
			return this;
		}

		@Override
		public @NonNull Appendable append(char c) throws IOException {
			appender.append(appendable, c);
			return this;
		}

	}

}
