package io.jstach.jstachio;

import io.jstach.jstachio.escapers.PlainText;

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
 * @see Escaper
 */
public sealed interface Appender permits Escaper {

	/**
	 * Analogous to {@link Appendable#append(CharSequence)}.
	 * @param <A> output type
	 * @param <E> exception type
	 * @param a appendable to write to. Always non null.
	 * @param s unlike appendable always non null.
	 * @throws E if an error happens while writting to the appendable
	 * @apiNote Implementations are required to implement this method.
	 */
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence s) throws E;

	/**
	 * Analogous to {@link Appendable#append(CharSequence, int, int)}.
	 * @param <A> output type
	 * @param <E> exception type
	 * @param a appendable to write to. Never null.
	 * @param csq Unlike appendable never null.
	 * @param start start inclusive
	 * @param end end exclusive
	 * @throws E if an error happens while writting to the appendable
	 * @apiNote Implementations are required to implement this method.
	 */
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence csq, int start, int end) throws E;

	/**
	 * Appends a character to the output.
	 * @param <A> output type
	 * @param <E> exception type
	 * @param a appendable to write to. Never null.
	 * @param c character
	 * @throws E if an error happens while writting to the appendable
	 * @apiNote Implementations are required to implement this method.
	 */
	public <A extends Output<E>, E extends Exception> void append(A a, char c) throws E;

	/**
	 * Appends a short to the output.
	 * @param <A> output type
	 * @param <E> exception type
	 * @param a appendable to write to. Never null.
	 * @param s short
	 * @throws E if an error happens while writting to the appendable
	 */
	public <A extends Output<E>, E extends Exception> void append(A a, short s) throws E;

	/**
	 * Appends an int to the output.
	 * <p>
	 * Implementations should override if they want different behavior or able to support
	 * appendables that can write the native type.
	 * @param <A> output type
	 * @param <E> exception type
	 * @param a appendable to write to. Never null.
	 * @param i int
	 * @throws E if an error happens while writting to the appendable
	 */
	public <A extends Output<E>, E extends Exception> void append(A a, int i) throws E;

	/**
	 * Appends a long to the output.
	 * <p>
	 * Implementations should override if they want different behavior or able to support
	 * appendables that can write the native type.
	 * @param <A> output type
	 * @param <E> exception type
	 * @param a appendable to write to. Never null.
	 * @param l long
	 * @throws E if an error happens while writting to the appendable
	 */
	public <A extends Output<E>, E extends Exception> void append(A a, long l) throws E;

	/**
	 * Appends a double to the output.
	 * <p>
	 * Implementations should override if they want different behavior or able to support
	 * appendables that can write the native type.
	 * @param <A> output type
	 * @param <E> exception type
	 * @param a appendable to write to. Never null.
	 * @param d double
	 * @throws E if an error happens while writting to the appendable
	 */
	public <A extends Output<E>, E extends Exception> void append(A a, double d) throws E;

	/**
	 * Appends a boolean to the output.
	 * <p>
	 * Implementations should override if they want different behavior or able to support
	 * appendables that can write the native type.
	 * @param <A> output type
	 * @param <E> exception type
	 * @param a appendable to write to. Never null.
	 * @param b boolean
	 * @throws E if an error happens while writting to the appendable
	 */
	public <A extends Output<E>, E extends Exception> void append(A a, boolean b) throws E;

	/**
	 * Default appender simply passes the contents unchanged to the Appendable.
	 * @return a passthrough appender
	 */
	public static Appender defaultAppender() {
		return PlainText.provider();
	}

}
