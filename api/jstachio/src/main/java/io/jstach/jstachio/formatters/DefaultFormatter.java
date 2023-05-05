package io.jstach.jstachio.formatters;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStacheFormatter;
import io.jstach.jstache.JStacheFormatterTypes;
import io.jstach.jstachio.Appender;
import io.jstach.jstachio.Formatter;
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
	default <A extends Appendable, APPENDER extends Appender<A>> void format(APPENDER downstream, A a, String path,
			Class<?> c, @Nullable Object o) throws IOException {
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
	default <A extends Appendable, APPENDER extends Appender<A>> void format(APPENDER downstream, A a, String path,
			String s) throws IOException {
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
		return DefaultFormatterSingleton.DefaultFormatter;
	}

}

enum DefaultFormatterSingleton implements DefaultFormatter {

	DefaultFormatter;

}