package io.jstach.jstachio;

import java.io.IOException;

/**
 * A singleton like decorator for appendables that has additional methods for dealing with
 * native types used to output variables that have been formatted. This interface is
 * mostly an internal detail for performance and generally direct implementations are
 * unnecessary.
 *
 * <p>
 * When a template outputs an <strong>escaped</strong> variable the callstack is as
 * follows:
 *
 * <pre>
 * formatter --&gt; escaper --&gt; appendable
 * </pre>
 *
 * When a template outputs an <strong>unescaped</strong> variable the callstack is as
 * follows:
 *
 * <pre>
 * formatter --&gt; appender --&gt; appendable
 * </pre>
 *
 * When a template outputs anything else (e.g. HTML markup) it writes directly to the
 * appendable.
 *
 * @apiNote <strong>Important:</strong> <em> The interface while public is currently
 * sealed. If you would like to see it unsealed to allow control of intercepting unescaped
 * variable output please file an issue.</em> Unlike an Appendable this class is expected
 * to be reused so state should be avoided and implementations should be thread safe.
 * @author agentgt
 * @param <A> the appendable
 * @see Escaper
 */
public sealed interface Appender permits Escaper, DefaultAppender {

	/**
	 * Analogous to {@link Appendable#append(CharSequence)}.
	 * @param a appendable to write to. Always non null.
	 * @param s unlike appendable always non null.
	 * @throws IOException if an error happens while writting to the appendable
	 * @apiNote Implementations are required to implement this method.
	 */
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence s) throws E;

	/**
	 * Analogous to {@link Appendable#append(CharSequence, int, int)}.
	 * @param a appendable to write to. Never null.
	 * @param csq Unlike appendable never null.
	 * @param start start inclusive
	 * @param end end exclusive
	 * @throws E if an error happens while writting to the appendable
	 * @apiNote Implementations are required to implement this method.
	 */
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence csq, int start, int end) throws E;

	/**
	 * Appends a character to an appendable.
	 * @param a appendable to write to. Never null.
	 * @param c character
	 * @throws E if an error happens while writting to the appendable
	 * @apiNote Implementations are required to implement this method.
	 */
	public <A extends Output<E>, E extends Exception> void append(A a, char c) throws E;

	/**
	 * Write a short by using {@link String#valueOf(int)}
	 * @param a appendable to write to. Never null.
	 * @param s short
	 * @throws E if an error happens while writting to the appendable
	 */
	default <A extends Output<E>, E extends Exception> void append(A a, short s) throws E {
		append(a, String.valueOf(s));
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
	default <A extends Output<E>, E extends Exception> void append(A a, int i) throws E {
		append(a, String.valueOf(i));
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
	default <A extends Output<E>, E extends Exception> void append(A a, long l) throws E {
		append(a, String.valueOf(l));
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
	default <A extends Output<E>, E extends Exception> void append(A a, double d) throws E {
		append(a, String.valueOf(d));
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
	default <A extends Output<E>, E extends Exception> void append(A a, boolean b) throws E {
		append(a, String.valueOf(b));
	}

	// /**
	// * Decorates an appendable with this appender such that the returned appendable will
	// * call the this appender which will then write to the inputted appendable.
	// * @param appendable never null.
	// * @return Appendable never null.
	// */
	// default Appendable toAppendable(A appendable) {
	// return new AppenderAppendable<>(this, appendable);
	// }

	/**
	 * Default appender simply passes the contents unchanged to the Appendable.
	 * @return a passthrough appender
	 */
	public static Appender defaultAppender() {
		return DefaultAppender.INSTANCE;
	}

	//
	// /**
	// * An appender that will directly call StringBuilder methods for native types.
	// * <p>
	// * This is a low level utility appender for where performance matters.
	// * @return an appender specifically for {@link StringBuilder}
	// */
	// public static Appender<StringBuilder> stringAppender() {
	// return StringAppender.INSTANCE;
	// }

}

/**
 * Default appender simply passes the contents unchanged to the Appendable.
 * @author agentgt
 *
 */
enum DefaultAppender implements Appender {

	/**
	 * Singleton instance
	 */
	INSTANCE;

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence s) throws E {
		a.append(s);
	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence csq, int start, int end) throws E {
		a.append(csq, start, end);
	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, char c) throws E {
		a.append(c);
	}

}

/// **
// * An appender that will directly call StringBuilder methods for native types.
// * <p>
// * This is a low level utility class for where performance matters.
// *
// * @author agentgt
// *
// */
// enum StringAppender implements Appender {
//
// /**
// * Singleton instance
// */
// INSTANCE;
//
// /**
// * {@inheritDoc}
// */
// @Override
// public <A extends Output<E>, E extends Exception> void append(StringBuilder a,
/// CharSequence s) {
// a.append(s);
// }
//
// /**
// * {@inheritDoc}
// */
// @Override
// public <A extends Output<E>, E extends Exception> void append(StringBuilder a,
/// CharSequence csq, int start, int end) {
// a.append(csq, start, end);
// }
//
// /**
// * {@inheritDoc}
// */
// @Override
// public <A extends Output<E>, E extends Exception> void append(StringBuilder a, char c)
/// {
// a.append(c);
// }
//
// /**
// * {@inheritDoc}
// */
// @Override
// public <A extends Output<E>, E extends Exception> void append(StringBuilder a, short s)
/// {
// a.append(s);
// }
//
// /**
// * {@inheritDoc}
// */
// public <A extends Output<E>, E extends Exception> void append(StringBuilder a, int i) {
// a.append(i);
// }
//
// /**
// * {@inheritDoc}
// */
// public <A extends Output<E>, E extends Exception> void append(StringBuilder a, long l)
/// {
// a.append(l);
// }
//
// /**
// * {@inheritDoc}
// */
// public <A extends Output<E>, E extends Exception> void append(StringBuilder a, double
/// d) {
// a.append(d);
// }
//
// /**
// * {@inheritDoc}
// */
// public <A extends Output<E>, E extends Exception> void append(StringBuilder a, boolean
/// b) {
// a.append(b);
// }
//
// }

// class AppenderAppendable<A extends Appendable> implements Appendable {
//
// private final Appender appender;
//
// private final A appendable;
//
// public AppenderAppendable(Appender<A> appender, A appendable) {
// super();
// this.appender = appender;
// this.appendable = appendable;
// }
//
// @Override
// public @NonNull Appendable append(@Nullable CharSequence csq) throws @Nullable E {
// appender.append(appendable, csq);
// return this;
// }
//
// @Override
// public @NonNull Appendable append(@Nullable CharSequence csq, int start, int end)
// throws E {
// appender.append(appendable, csq, start, end);
// return this;
// }
//
// @Override
// public @NonNull Appendable append(char c) throws E {
// appender.append(appendable, c);
// return this;
// }
//
// }
