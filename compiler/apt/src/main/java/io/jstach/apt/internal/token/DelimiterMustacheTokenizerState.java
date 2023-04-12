package io.jstach.apt.internal.token;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.MustacheToken;
import io.jstach.apt.internal.ProcessingException;
import io.jstach.apt.internal.token.Delimiters.DelimiterParsingException;

class DelimiterMustacheTokenizerState implements MustacheTokenizerState {

	private final MustacheTokenizer tokenizer;

	private StringBuilder delimiterContent = new StringBuilder();

	private @Nullable Delimiters previousDelimiters = null;

	private State state = State.OPEN;

	private enum State {

		OPEN, EXPECTING_CLOSE

	}

	public DelimiterMustacheTokenizerState(MustacheTokenizer tokenizer) {
		super();
		this.tokenizer = tokenizer;
	}

	@Override
	public @Nullable Void twoOpenBraces() throws ProcessingException {
		tokenizer.error("Unexpected open braces");
		return null;
	}

	@Override
	public @Nullable Void twoClosingBraces() throws ProcessingException {
		try {
			previousDelimiters = tokenizer.getDelimiters();
			var delims = Delimiters.of(delimiterContent.toString());
			tokenizer.setDelimiters(delims);
			tokenizer.setState(new OutsideMustacheTokenizerState(tokenizer));
			return null;
		}
		catch (DelimiterParsingException e) {
			tokenizer.error(e.getMessage());
			return null;
		}
	}

	@Override
	public @Nullable Void threeOpenBraces() throws ProcessingException {
		tokenizer.error("Unexpected open braces");
		return null;
	}

	@Override
	public @Nullable Void threeClosingBraces() throws ProcessingException {
		tokenizer.error("Unexpected three closing braces");
		return null;
	}

	@Override
	public @Nullable Void character(char c) throws ProcessingException {

		return switch (state) {
			case EXPECTING_CLOSE: {
				if (Character.isWhitespace(c)) {
					yield null;
				}
				else {
					tokenizer.error("Unexpected '=' in delimiter definition");
					yield null;
				}

			}
			case OPEN: {
				if (c == '=') {
					state = State.EXPECTING_CLOSE;
					yield null;
				}
				else {
					delimiterContent.append(c);
					yield null;
				}
			}
		};
	}

	@Override
	public @Nullable Void endOfFile() throws ProcessingException {
		tokenizer.error("Unclosed field at the end of file");
		return null;
	}

	@Override
	public void beforeStateChange() throws ProcessingException {
		// TODO we need to emit a token so that white space cleanup happens
		var prev = previousDelimiters;
		if (prev == null) {
			throw new IllegalStateException("bug previousDelimiters");
		}
		tokenizer.emitToken(new MustacheToken.DelimitersToken(prev, tokenizer.getDelimiters()));

	}

}
