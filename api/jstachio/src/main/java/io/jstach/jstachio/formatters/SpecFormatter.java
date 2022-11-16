package io.jstach.jstachio.formatters;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStacheFormatter;
import io.jstach.jstachio.Appender;
import io.jstach.jstachio.Formatter;
import io.jstach.jstachio.context.ContextNode;

/**
 * Formatter that follows the spec rules that if a variable is <code>null</code> it will
 * be an empty string (ie NOOP).
 */
@JStacheFormatter
public enum SpecFormatter implements Formatter {

	/**
	 * Formatter that follows the spec rules that if a variable is a missing it will be an
	 * empty string (ie NOOP).
	 */
	INSTANCE {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public <A extends Appendable, APPENDER extends Appender<A>> void format(APPENDER downstream, A a, String path,
				Class<?> c, @Nullable Object o) throws IOException {
			if (o instanceof ContextNode m) {
				downstream.append(a, m.renderString());
			}
			else if (o != null) {
				downstream.append(a, String.valueOf(o));
			}

		}
	};

	/**
	 * Provides the formatter for static lookup.
	 * @return the single instance of the spec formatter
	 */
	public static Formatter provides() {
		return INSTANCE;
	}

}