package io.jstach.examples.formatter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStacheFormatter;
import io.jstach.jstache.JStacheFormatterTypes;
import io.jstach.jstachio.Appender;
import io.jstach.jstachio.Formatter;
import io.jstach.jstachio.formatters.DefaultFormatter;

@JStacheFormatter
@JStacheFormatterTypes(types = LocalDate.class)
public class MyFormatter implements Formatter {

	private static final Formatter INSTANCE = new MyFormatter();

	@Override
	public <A extends Appendable, APPENDER extends Appender<A>> void format(APPENDER downstream, A a, String path,
			Class<?> c, @Nullable Object o) throws IOException {
		if (o instanceof LocalDate ld) {
			downstream.append(a, ld.format(DateTimeFormatter.ISO_DATE));
			return;
		}
		DefaultFormatter.provider().format(downstream, a, path, c, o);
	}

	public static Formatter provider() {
		return INSTANCE;
	}

}
