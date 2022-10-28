package io.jstach.formatters;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.Appender;
import io.jstach.Formatter;
import io.jstach.annotation.JStacheFormatter;
import io.jstach.context.ContextNode;

/**
 * Formatter that follows the spec rules that if a variable is a missing it will be an
 * empty string (ie NOOP).
 */
@JStacheFormatter
public enum SpecFormatter implements Formatter {

	/**
	 * Formatter that follows the spec rules that if a variable is a missing it will be an
	 * empty string (ie NOOP).
	 */
	INSTANCE {
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

	public static Formatter provides() {
		return INSTANCE;
	}

}