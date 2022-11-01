package io.jstach.formatters;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.Appender;
import io.jstach.Formatter;
import io.jstach.annotation.JStacheFormatter;
import io.jstach.context.ContextNode;

/**
 * Default formatters.
 *
 * @author agentgt
 */
@JStacheFormatter
public enum DefaultFormatter implements Formatter {

	/**
	 * Default formatter.
	 *
	 * Unlike the mustache spec it will throw a NPE trying to format null objects.
	 */
	INSTANCE {
		/**
		 * {@inheritDoc} Will throw an NPE if parameter o is <code>null</code>.
		 */
		@Override
		public <A extends Appendable, APPENDER extends Appender<A>> void format(APPENDER downstream, A a, String path,
				Class<?> c, @Nullable Object o) throws IOException {
			if (o == null) {
				throw new NullPointerException("null at: " + path);
			}
			else if (o instanceof ContextNode m) {
				downstream.append(a, m.renderString());
			}
			else {
				downstream.append(a, String.valueOf(o));
			}
		}
	};

	/**
	 * Provides the default formatter for static lookup.
	 * @return the default formatter singleton
	 */
	public static Formatter provides() {
		return INSTANCE;
	}

}