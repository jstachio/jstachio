package io.jstach.jstachio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStacheContentType;

/**
 * An Escaper is an {@link Appender} used to escape content such as HTML. A
 * {@link Formatter} is usually what will call the Escaper and like a formatter should be
 * singleton like and expect reuse.
 *
 * @see JStacheContentType
 * @author agentgt
 */
public interface Escaper extends Appender<Appendable>, Function<String, String> {

	/**
	 * Escapes a String by using StringBuilder and calling
	 * {@link #append(Appendable, CharSequence)}.
	 * @param t String to ge escaped.
	 * @return escaped content
	 * @throws UncheckedIOException if the appender or appendable throw an
	 * {@link IOException}
	 */
	@Override
	default String apply(String t) throws UncheckedIOException {
		StringBuilder sb = new StringBuilder();
		try {
			append(sb, t);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return sb.toString();
	}

	/**
	 * Adapts a function to an Escaper.
	 *
	 * If the function is already an Escaper then it is simply returned (noop). Thus it is
	 * safe to repeatedly call this on Escaper. If the function is adapted the returned
	 * adapted Escaper does not pass native types to the inputted function.
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
	public void append(Appendable a, CharSequence s) throws IOException {
		a.append(function.apply(String.valueOf(s)));
	}

	@Override
	public void append(Appendable a, @Nullable CharSequence csq, int start, int end) throws IOException {
		if (csq == null) {
			a.append(function.apply("null"));
			return;
		}
		a.append(function.apply(String.valueOf(csq.subSequence(start, end))));
	}

	@Override
	public void append(Appendable a, char c) throws IOException {
		a.append(function.apply(String.valueOf(c)));
	}

}
