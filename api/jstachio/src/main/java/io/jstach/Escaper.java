package io.jstach;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

import io.jstach.annotation.JStacheContentType;
import org.eclipse.jdt.annotation.Nullable;

/**
 * An Escaper is an {@link Appender} used to escape content such as HTML. A
 * {@link Formatter} is usually what will call the Escaper and like a formatter should be
 * singleton like and expect reuse.
 *
 * @see JStacheContentType
 * @author agentgt
 */
public interface Escaper extends Appender<Appendable>, Function<String, String> {

	@Override
	default String apply(String t) {
		StringBuilder sb = new StringBuilder();
		try {
			append(sb, t);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return sb.toString();
	}

	enum NoEscaper implements Escaper {

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
