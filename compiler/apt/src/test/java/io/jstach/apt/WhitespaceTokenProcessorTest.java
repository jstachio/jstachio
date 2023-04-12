package io.jstach.apt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

import io.jstach.apt.WhitespaceTokenProcessor.ProcessToken;
import io.jstach.apt.internal.LoggingSupport;
import io.jstach.apt.internal.MustacheToken;
import io.jstach.apt.internal.PositionedToken;
import io.jstach.apt.internal.ProcessingException;
import io.jstach.apt.internal.TokenProcessor;
import io.jstach.apt.internal.token.MustacheTokenizer;

public class WhitespaceTokenProcessorTest {

	PrintStream out = System.out;

	@Test
	public void test() throws ProcessingException, IOException {
		String template = """
				{{<layout.mustache}}
				{{$body}}<span>{{message}}</span>{{/body}}
				{{/layout.mustache}}
				a
				""";

		assertTrue(template.endsWith("\n"));
		var w = new WhitespaceLogger();
		try {
			w.run(NamedReader.ofString(template));
		}
		catch (ProcessingException e) {
			throw new IOException(e.getMessage() + " " + e.position(), e);
		}

	}

	@Test
	public void testDelimiters() throws ProcessingException, IOException {
		String template = """
				{{hello}}
				{{=<% %>=}}
				<% hello %>
				""";

		assertTrue(template.endsWith("\n"));
		var w = new WhitespaceLogger();
		try {
			w.run(NamedReader.ofString(template));
		}
		catch (ProcessingException e) {
			throw new IOException(e.getMessage() + " " + e.position(), e);
		}
		String actual = printTokens(w.tokens);

		String expected = """
				NORMAL TagToken[tagKind=VARIABLE, name=hello]
				NORMAL NewlineToken[newlineChar=LF]
				NORMAL NewlineToken[newlineChar=LF]
				NORMAL TagToken[tagKind=VARIABLE, name=hello]
				NORMAL NewlineToken[newlineChar=LF]""";
		assertEquals(expected, actual);

	}

	static String printTokens(List<ProcessToken> tokens) {
		return tokens.stream().map(t -> t.hint() + " " + t.token().innerToken()).collect(Collectors.joining("\n"));
	}

	public static class WhitespaceLogger extends WhitespaceTokenProcessor {

		private final LoggingSupport logging = new LoggingSupport() {

			@Override
			public boolean isDebug() {
				return true;
			}

			@Override
			public void debug(CharSequence message) {
				System.out.println("[TEST] " + message);

			}

		};

		private List<ProcessToken> tokens = new ArrayList<>();

		public WhitespaceLogger() {
			super();
		}

		public void run(NamedReader reader) throws ProcessingException, IOException {
			TokenProcessor<@Nullable Character> processor = MustacheTokenizer.createInstance(reader.name(), this);
			int readResult;
			while ((readResult = reader.read()) >= 0) {
				try {
					processor.processToken((char) readResult);
				}
				catch (ProcessingException e) {
					if (logging.isDebug()) {
						debug(e.getMessage());
						e.printStackTrace();
					}
					throw e;
				}
			}
			processor.processToken(EOF);
		}

		@Override
		public LoggingSupport logging() {
			return logging;
		}

		@Override
		protected void processTokenGroup(List<ProcessToken> tokens) throws ProcessingException {
			debug("tokens: " + tokens.stream().map(t -> t.hint() + " " + t.token().innerToken())
					.collect(Collectors.joining(", ")));
			this.tokens.addAll(tokens);
			super.processTokenGroup(tokens);
		}

		@Override
		protected void handleToken(PositionedToken<MustacheToken> positionedToken) throws ProcessingException {
			// System.out.println(positionedToken.innerToken());

		}

	}

}
