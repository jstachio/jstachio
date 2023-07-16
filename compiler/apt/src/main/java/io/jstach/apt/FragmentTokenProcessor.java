package io.jstach.apt;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.LoggingSupport;
import io.jstach.apt.internal.MustacheToken;
import io.jstach.apt.internal.MustacheToken.TagToken;
import io.jstach.apt.internal.token.MustacheTokenizer;
import io.jstach.apt.internal.PositionedToken;
import io.jstach.apt.internal.ProcessingException;
import io.jstach.apt.internal.TokenProcessor;

public class FragmentTokenProcessor extends WhitespaceTokenProcessor {

	private final String fragment;
	private final LoggingSupport logging;
	private final StringBuilder content = new StringBuilder();
	private final Deque<Section> section = new ArrayDeque<>();
	private State state = State.OUTSIDE;

	private record Section(
			TagToken token, boolean isFragment) {}

	private enum State {
		INSIDE,
		OUTSIDE,
		FOUND,
		NOT_FOUND;
	}

	public FragmentTokenProcessor(
			String fragment,
			LoggingSupport logging) {
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
		return content.toString();
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
	
	@Override
	protected void processTokenGroup(
			List<ProcessToken> tokens)
			throws ProcessingException {
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
	boolean isFragmentEnd(ProcessToken token) {
		var mt = token.token().innerToken();
		var sec = section.peek();
		if (mt instanceof TagToken tt && tt.tagKind().isEndSection()) {
			return sec.isFragment();
		}
		return false;
	}

	@Override
	protected void handleToken(
			PositionedToken<MustacheToken> positionedToken)
			throws ProcessingException {
		
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
		else if( token.isEOF() && this.state != State.FOUND) {
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
			case OUTSIDE -> {
			}
			case FOUND, NOT_FOUND -> {
			}
		}
	}

	private void validateEndSection(
			String lastSection,
			TagToken tt)
			throws ProcessingException {
		if (lastSection == null) {
			throw new ProcessingException(position, "Close section for no open section: " + tt.name());
		}
		if (!lastSection.equals(tt.name())) {
			throw new ProcessingException(
					position,
					"Close section of: " + tt.name() + " does not match current open section: " + lastSection);
		}
	}


}
