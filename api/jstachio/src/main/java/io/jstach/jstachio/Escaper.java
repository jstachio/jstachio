package io.jstach.jstachio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheContentType;
import io.jstach.jstache.JStacheType;

/**
 * An Escaper is an {@link Appender} used to escape content such as HTML. A
 * {@link Formatter} is usually what will call the Escaper and like a formatter it should
 * be singleton like and expect reuse.
 * <p>
 * When a template outputs an <strong>escaped</strong> variable the callstack is as
 * follows:
 *
 * <pre>
 * formatter --&gt; escaper --&gt; appendable
 * </pre>
 *
 * Escapers are also a {@code Function<String,String>} to allow compatibility with
 * {@link JStacheType#STACHE zero dependency generated code} that expects Escapers to be
 * of type {@code Function<String,String>}.
 * <p>
 *
 * If performance is not a concern an easier way to create an implementation is to adapt a
 * function by using {@link #of(Function)}.
 * <p>
 * To implement a custom escaper:
 *
 * <ol>
 * <li>Implement this interface or use {@link #of(Function)}.</li>
 * <li>Register the custom escaper. See {@link JStacheContentType}.</li>
 * <li>Set {@link JStacheConfig#contentType()} to the class that has the
 * {@link JStacheContentType}.</li>
 * </ol>
 *
 * @apiNote Implementations should be threadsafe and expect reuse!
 * @see JStacheContentType
 * @author agentgt
 */
public non-sealed interface Escaper extends Appender, Function<String, String> {

	/**
	 * Escapes a String by using StringBuilder and calling
	 * {@link #append(Output, CharSequence)}.
	 * <p>
	 * This method is to make Escaper implementations compatible with
	 * {@link JStacheType#STACHE zero dependency generated code} that expects Escapers to
	 * be {@code Function<String,String>}.
	 * @param t String to ge escaped.
	 * @return escaped content
	 * @throws UncheckedIOException if the appender or appendable throw an
	 * {@link IOException}
	 */
	@Override
	default String apply(String t) throws UncheckedIOException {
		var out = new Output.StringOutput(new StringBuilder());
		append(out, t);
		return out.toString();
	}

	/**
	 * Escapes the characters if it needs it. {@inheritDoc}
	 */
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence s) throws E;

	/**
	 * Escapes the characters if it needs it. {@inheritDoc}
	 */
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence csq, int start, int end) throws E;

	/**
	 * Escapes the character if it needs escaping. {@inheritDoc}
	 */
	public <A extends Output<E>, E extends Exception> void append(A a, char c) throws E;

	/**
	 * Escapes the character if it needs escaping. The default implementation will
	 * {@link String#valueOf(short)} and call {@link #append(Output, CharSequence)}.
	 * {@inheritDoc}
	 */
	@Override
	default <A extends Output<E>, E extends Exception> void append(A a, short s) throws E {
		append(a, String.valueOf(s));
	}

	/**
	 * Escapes the character if it needs escaping. The default implementation will
	 * {@link String#valueOf(int)} and call {@link #append(Output, CharSequence)}.
	 * {@inheritDoc}
	 */
	@Override
	default <A extends Output<E>, E extends Exception> void append(A a, int i) throws E {
		append(a, String.valueOf(i));
	}

	/**
	 * Escapes the character if it needs escaping. The default implementation will
	 * {@link String#valueOf(long)} and call {@link #append(Output, CharSequence)}.
	 * {@inheritDoc}
	 */
	@Override
	default <A extends Output<E>, E extends Exception> void append(A a, long l) throws E {
		append(a, String.valueOf(l));
	}

	/**
	 * Escapes the character if it needs escaping. The default implementation will
	 * {@link String#valueOf(double)} and call {@link #append(Output, CharSequence)}.
	 * {@inheritDoc}
	 */
	@Override
	default <A extends Output<E>, E extends Exception> void append(A a, double d) throws E {
		append(a, String.valueOf(d));
	}

	/**
	 * Escapes the character if it needs escaping. The default implementation will
	 * {@link String#valueOf(boolean)} and call {@link #append(Output, CharSequence)}.
	 * {@inheritDoc}
	 */
	@Override
	default <A extends Output<E>, E extends Exception> void append(A a, boolean b) throws E {
		append(a, String.valueOf(b));
	}

	/**
	 * Adapts a function to an Escaper.
	 *
	 * If the function is already an Escaper then it is simply returned (noop). Thus it is
	 * safe to repeatedly call this on an Escaper. If the function is adapted the returned
	 * adapted Escaper will convert native types with {@code String.valueOf} first and
	 * then apply the escape function.
	 * @param escapeFunction returned if it is already an escaper
	 * @return adapted Escaper
	 */
	public static Escaper of(Function<String, String> escapeFunction) {
		if (escapeFunction instanceof Escaper e) {
			return e;
		}
		return new FunctionEscaper(escapeFunction);

	}

}

class FunctionEscaper implements Escaper {

	private final Function<String, String> function;

	public FunctionEscaper(Function<String, String> function) {
		super();
		this.function = function;
	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, CharSequence s) throws E {
		a.append(function.apply(s.toString()));
	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, @Nullable CharSequence csq, int start, int end)
			throws E {
		if (csq == null) {
			a.append(function.apply("null"));
			return;
		}
		a.append(function.apply(String.valueOf(csq.subSequence(start, end))));
	}

	@Override
	public <A extends Output<E>, E extends Exception> void append(A a, char c) throws E {
		append(a, String.valueOf(c));
	}

}
