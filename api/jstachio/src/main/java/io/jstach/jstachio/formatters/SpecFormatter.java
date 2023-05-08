package io.jstach.jstachio.formatters;

import java.net.URI;
import java.net.URL;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStacheFormatter;
import io.jstach.jstache.JStacheFormatterTypes;
import io.jstach.jstachio.Appender;
import io.jstach.jstachio.Formatter;
import io.jstach.jstachio.Output;
import io.jstach.jstachio.context.ContextNode;

/**
 * Formatter that follows the spec rules that if a variable is <code>null</code> it will
 * be an empty string (ie NOOP).
 */
@JStacheFormatter
@JStacheFormatterTypes(types = { URI.class, URL.class })
public interface SpecFormatter extends Formatter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	default <A extends Output<E>, E extends Exception> void format(Appender downstream, A a, String path, Class<?> c,
			@Nullable Object o) throws E {
		if (o instanceof ContextNode m) {
			downstream.append(a, m.renderString());
		}
		else if (o != null) {
			downstream.append(a, String.valueOf(o));
		}
	}

	/**
	 * {@inheritDoc} <strong> Unlike the normal behavior of {@link String#valueOf(Object)}
	 * if a String is null then nothing will be rendered per the mustache spec. </strong>.
	 */
	@Override
	default <A extends Output<E>, E extends Exception> void format(Appender downstream, A a, String path, String s)
			throws E {
		if (s != null) {
			downstream.append(a, s);
		}
	}

	/**
	 * Provides the formatter for static lookup.
	 * @return the single instance of the spec formatter
	 */
	public static Formatter provider() {
		return SpecFormatterSingleton.SpecFormatter;
	}

}

enum SpecFormatterSingleton implements SpecFormatter {

	SpecFormatter;

}