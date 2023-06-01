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
 * Default formatters.
 *
 * Unlike the mustache spec it will throw a NPE trying to format null objects.
 *
 * @author agentgt
 */
@JStacheFormatter
@JStacheFormatterTypes(types = { URI.class, URL.class })
public interface DefaultFormatter extends Formatter {

	/**
	 * {@inheritDoc} Will throw an NPE if parameter o is <code>null</code>.
	 */
	@Override
	default <A extends Output<E>, E extends Exception> void format(Appender downstream, A a, String path, Class<?> c,
			@Nullable Object o) throws E {
		if (o == null) {
			throw new NullPointerException("null at: '" + path + "'");
		}
		else if (o instanceof ContextNode m) {
			downstream.append(a, m.renderString());
		}
		else {
			downstream.append(a, String.valueOf(o));
		}
	}

	/**
	 * {@inheritDoc} Will throw an NPE if parameter s is <code>null</code>.
	 */
	@Override
	default <A extends Output<E>, E extends Exception> void format(Appender downstream, A a, String path, String s)
			throws E {
		if (s == null) {
			throw new NullPointerException("null at: '" + path + "'");
		}
		downstream.append(a, s);
	}

	/**
	 * Provides the default formatter for static lookup.
	 * @return the default formatter singleton
	 */
	public static Formatter provider() {
		return DefaultFormatterSingleton.DEFAULT_FORMATTER;
	}

	/**
	 * Provides the default formatter for static lookup.
	 * @return the default formatter singleton
	 */
	public static Formatter of() {
		return DefaultFormatterSingleton.DEFAULT_FORMATTER;
	}

}

enum DefaultFormatterSingleton implements DefaultFormatter {

	DEFAULT_FORMATTER;

}