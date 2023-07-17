package io.jstach.apt;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.WhitespaceTokenProcessor.ProcessToken.ProcessHint;
import io.jstach.apt.internal.CodeAppendable;
import io.jstach.apt.internal.LoggingSupport;
import io.jstach.apt.internal.MustacheToken;
import io.jstach.apt.internal.MustacheToken.TagToken;
import io.jstach.apt.internal.PositionedToken;
import io.jstach.apt.internal.ProcessingException;
import io.jstach.apt.internal.TokenProcessor;
import io.jstach.apt.internal.token.MustacheTokenizer;

class FragmentTokenProcessor extends WhitespaceTokenProcessor {

	private final String fragment;

	private final LoggingSupport logging;

	private final StringBuilder content = new StringBuilder();

	private final Deque<Section> section = new ArrayDeque<>();

	private State state = State.OUTSIDE;

	private String indent = "";

	private String processed = "";

	private record Section(TagToken token, boolean isFragment) {
	}

	private enum State {

		INSIDE, OUTSIDE, FOUND, NOT_FOUND;

	}

	public FragmentTokenProcessor(String fragment, LoggingSupport logging) {
		super();
		this.fragment = fragment;
		this.logging = logging;
	}

	public String run(NamedReader reader) throws ProcessingException, IOException {
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
		processed = content.toString();
		return reindent(processed, indent);
	}

	@Override
	public LoggingSupport logging() {
		return this.logging;
	}

	@Nullable
	String lastSection() {
		var s = section.peek();
		if (s == null)
			return null;
		return s.token().name();
	}

	public String getIndent() {
		return indent;
	}

	public boolean wasFound() {
		return State.FOUND == state;
	}

	static String reindent(String content, String indent) {
		if (indent.isEmpty()) {
			return content;
		}
		var lines = CodeAppendable.split(content, "\n");
		StringBuilder sb = new StringBuilder();
		for (var line : lines) {
			if (line.equals("\n") || line.equals("\r\n")) {
				sb.append(line);
			}
			else if (line.startsWith(indent)) {
				sb.append(line.substring(indent.length()));
			}
			else {
				return content;
			}
		}
		return sb.toString();
	}

	@Override
	protected void processTokenGroup(List<ProcessToken> tokens) throws ProcessingException {
		if (hasFragmentStart(tokens.stream())) {
			var first = tokens.get(0);
			if (first.hint() == ProcessHint.INDENT) {
				StringBuilder leadingSpace = new StringBuilder();
				first.token().innerToken().appendRawText(leadingSpace);
				this.indent = leadingSpace.toString();
			}
		}
		if (this.state == State.INSIDE) {
			if (hasFragmentEnd(tokens)) {
				processTokenGroup(this::handleToken, tokens);
			}
			else {
				for (var t : tokens) {
					handleToken(t.token());
				}
			}
		}
		else {
			super.processTokenGroup(tokens);
		}
	}

	boolean hasFragmentEnd(Iterable<ProcessToken> tokens) {
		for (var pt : tokens) {
			if (isFragmentEnd(pt)) {
				return true;
			}
		}
		return false;
	}

	boolean hasFragmentStart(Stream<ProcessToken> tokens) {
		return tokens.filter(this::isFragmentStart).findFirst().isPresent();
	}

	boolean isFragmentEnd(ProcessToken token) {
		var mt = token.token().innerToken();
		var sec = section.peek();
		if (sec != null && mt instanceof TagToken tt && tt.tagKind().isEndSection()) {
			return sec.isFragment();
		}
		return false;
	}

	boolean isFragmentStart(ProcessToken token) {
		if (this.state != State.OUTSIDE) {
			return false;
		}
		var mt = token.token().innerToken();

		if (mt instanceof TagToken tt && tt.tagKind().isBeginSection() && tt.name().equals(fragment)) {
			return true;
		}
		return false;
	}

	@Override
	protected void handleToken(PositionedToken<MustacheToken> positionedToken) throws ProcessingException {

		if (state == State.FOUND) {
			return;
		}

		var token = positionedToken.innerToken();
		String lastSection = lastSection();

		final State nextState;
		if (token instanceof TagToken tt) {
			String tagName = tt.name();
			boolean isFragmentName = fragment.equals(tagName);

			if (tt.tagKind().isBeginSection()) {
				if (isFragmentName && state == State.OUTSIDE) {
					section.push(new Section(tt, true));
					nextState = State.INSIDE;
				}
				else {
					section.push(new Section(tt, false));
					nextState = this.state;
				}

			}
			else if (tt.tagKind().isEndSection()) {
				validateEndSection(lastSection, tt);
				var sec = section.pop();
				if (sec.isFragment()) {
					nextState = State.FOUND;
				}
				else {
					nextState = this.state;
				}
			}
			else {
				nextState = this.state;
			}
		}
		else if (token.isEOF() && this.state != State.FOUND) {
			nextState = State.NOT_FOUND;
		}
		else {
			nextState = this.state;
		}
		var lastState = this.state;
		this.state = nextState;
		switch (nextState) {
			case INSIDE -> {
				if (lastState != State.OUTSIDE) {
					token.appendRawText(content);
				}
			}
			case OUTSIDE, FOUND, NOT_FOUND -> {
				// Sonar.. I am almost done with you. I want exhaustive here.
			}
		}
	}

	private void validateEndSection(String lastSection, TagToken tt) throws ProcessingException {
		if (lastSection == null) {
			throw new ProcessingException(position, "Close section for no open section: " + tt.name());
		}
		if (!lastSection.equals(tt.name())) {
			throw new ProcessingException(position,
					"Close section of: " + tt.name() + " does not match current open section: " + lastSection);
		}
	}

}
