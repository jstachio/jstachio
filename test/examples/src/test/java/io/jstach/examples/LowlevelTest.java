package io.jstach.examples;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.jstach.jstache.JStache;
import io.jstach.jstachio.Appender;
import io.jstach.jstachio.Escaper;
import io.jstach.jstachio.Formatter;
import io.jstach.jstachio.formatters.DefaultFormatter;

public class LowlevelTest {

	@JStache(template = """
			{{s}}
			{{i}}
			{{l}}
			{{d}}
			{{b}}
			----
			{{{s}}}
			{{{i}}}
			{{{l}}}
			{{{d}}}
			{{{b}}}
			""")
	public record Natives(String s, int i, long l, double d, boolean b) {
	}

	@Test
	public void testRawCall() throws Exception {
		Natives data = new Natives("s", 0, 0, 0.0d, true);

		StringBuilder unescapedWriter = new StringBuilder();
		Formatter formatter = DefaultFormatter.provider();
		var escaper = Appender.stringAppender();
		Appender<StringBuilder> appender = Appender.stringAppender();

		NativesRenderer.render(data, unescapedWriter, formatter, escaper, appender);

		String actual = unescapedWriter.toString();

		String expected = """
				s
				0
				0
				0.0
				true
				----
				s
				0
				0
				0.0
				true
				""";

		assertEquals(expected, actual);

	}

	@Test
	public void testRawCallWithEscaper() throws Exception {
		Natives data = new Natives("s", 0, 0, 0.0d, true);

		StringBuilder unescapedWriter = new StringBuilder();
		Formatter formatter = DefaultFormatter.provider();
		Escaper escaper = Escaper.of(s -> "escaped: " + s);
		Appender<StringBuilder> appender = Appender.stringAppender();

		NativesRenderer.render(data, unescapedWriter, formatter, escaper, appender);

		String actual = unescapedWriter.toString();

		String expected = """
				escaped: s
				escaped: 0
				escaped: 0
				escaped: 0.0
				escaped: true
				----
				s
				0
				0
				0.0
				true
				""";

		assertEquals(expected, actual);

	}

}
