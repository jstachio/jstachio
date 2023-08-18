package io.jstach.apt;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.LoggingSupport;
import io.jstach.apt.internal.MustacheToken;
import io.jstach.apt.internal.MustacheToken.TextToken;
import io.jstach.apt.internal.MustacheTokenProcessor;
import io.jstach.apt.internal.Position;
import io.jstach.apt.internal.PositionedToken;
import io.jstach.apt.internal.ProcessingException;

abstract class WhitespaceTokenProcessor implements MustacheTokenProcessor, LoggingSupport.LoggingSupplier {

	private Deque<PositionedToken<MustacheToken>> previousTokens = new ArrayDeque<>(5);

	protected boolean atStartOfLine = true;

	protected @Nullable PositionedToken<MustacheToken> currentToken = null;

	protected @Nullable PositionedToken<MustacheToken> lastProcessedToken = null;

	protected Position position = Position.noPosition();

	protected String partialIndent = "";

	@Override
	public final void processToken(PositionedToken<MustacheToken> positionedToken) throws ProcessingException {
		var tokens = filter(positionedToken);
		for (var t : tokens) {
			previousTokens.offer(t);
			processTokens();
		}
	}

	protected List<PositionedToken<MustacheToken>> filter(PositionedToken<MustacheToken> positionedToken)
			throws ProcessingException {
		return List.of(positionedToken);
	}

	private void processTokens() throws ProcessingException {

		boolean eof = previousTokens.stream().filter(t -> t.innerToken().isEOF()).findFirst().isPresent();
		if (eof) {
			if (!previousTokens.getLast().innerToken().isEOF()) {
				throw new IllegalStateException(previousTokens.toString());
			}
		}

		/*
		 * For standalone tag line support we need to see if blank space is around the
		 * tag.
		 *
		 * That is four tokens max:
		 *
		 * [ space* ] {{#some section}} [ space* ] [ newline ]
		 *
		 * Beginning of the file case: {{#some section}} [ space* ] [ newline ]
		 */

		Deque<PositionedToken<MustacheToken>> buf = new ArrayDeque<>();

		do {

			buf.clear();

			int size = previousTokens.size();

			if (size == 1 && eof) {
				_processToken(previousTokens.pop());
				return;
			}

			if (size < 2 && !eof) {
				return; // we need more tokens
			}

			var firstToken = previousTokens.pop();
			var secondToken = previousTokens.pop();
			buf.offerLast(firstToken);
			buf.offerLast(secondToken);

			/*
			 * Handle the easiest negative case [ not new line or space ] {{#somesection}}
			 */
			if (atStartOfLine
					&& !(firstToken.innerToken().isNewlineToken() || firstToken.innerToken().isWhitespaceToken())
					&& secondToken.innerToken().isStandaloneToken()) {
				processTokenGroup(ProcessToken.of(firstToken), ProcessToken.of(secondToken));
				atStartOfLine = false;
				continue;
			}
			/*
			 * {{#some section}} [ newline ]
			 */
			if (atStartOfLine && firstToken.innerToken().isStandaloneToken()
					&& secondToken.innerToken().isNewlineOrEOF()) {
				debug("2 standalone condition: {{#some section}} [ newline ]");
				processTokenGroup(ProcessToken.of(firstToken), ProcessToken.ignore(secondToken));
				atStartOfLine = true;
				continue;
			}

			if (atStartOfLine && size >= 3) {
				var thirdToken = previousTokens.pop();
				buf.add(thirdToken);

				/*
				 * {{#some section}} [white space] [ newline ]
				 */
				if (firstToken.innerToken().isStandaloneToken() //
						&& secondToken.innerToken().isWhitespaceToken() //
						&& thirdToken.innerToken().isNewlineOrEOF()) {
					debug("3 standalone condition: {{#some section}} [white space] [ newline ]");
					processTokenGroup(ProcessToken.of(firstToken), ProcessToken.ignore(secondToken),
							ProcessToken.ignore(thirdToken));
					atStartOfLine = true;
					continue;
				}

				/*
				 * [white space] {{#some section}} [ newline ]
				 */
				if (firstToken.innerToken().isWhitespaceToken() //
						&& secondToken.innerToken().isStandaloneToken() //
						&& thirdToken.innerToken().isNewlineOrEOF()) {
					debug("3 standalone condition: [white space] {{#some section}} [ newline ]");
					processTokenGroup(ProcessToken.indent(firstToken), ProcessToken.of(secondToken),
							ProcessToken.ignore(thirdToken));
					atStartOfLine = true;
					continue;
				}

				if (size >= 4) {
					var fourthToken = previousTokens.pop();
					buf.add(fourthToken);

					/*
					 * [white space] {{#some section}} [white space] [ newline ]
					 */
					if (firstToken.innerToken().isWhitespaceToken() //
							&& secondToken.innerToken().isStandaloneToken() //
							&& thirdToken.innerToken().isWhitespaceToken() //
							&& fourthToken.innerToken().isNewlineOrEOF()) {
						debug("4 standalone condition: [white space] {{#some section}} [white space] [ newline ]");
						processTokenGroup(ProcessToken.indent(firstToken), ProcessToken.of(secondToken),
								ProcessToken.ignore(thirdToken), ProcessToken.ignore(fourthToken));
						atStartOfLine = true;
						continue;
					}
				}
			}
			// We have to put the tokens back into the queue if none of the conditions
			// applied
			buf.descendingIterator().forEachRemaining(previousTokens::offerFirst);

			if (eof && !previousTokens.isEmpty()) {
				processTokenGroup(ProcessToken.of(previousTokens.pop()));
			}
			else if (previousTokens.size() > 5) {
				processTokenGroup(ProcessToken.of(previousTokens.pop()));
			}

		}
		while (eof && !previousTokens.isEmpty());
	}

	protected void processTokenGroup(@NonNull ProcessToken... tokens) throws ProcessingException {
		processTokenGroup(List.of(tokens));
	}

	protected void processTokenGroup(List<ProcessToken> tokens) throws ProcessingException {
		this.processTokenGroup(this::_processToken, tokens);
	}

	protected void processTokenGroup(MustacheTokenProcessor processor, List<ProcessToken> tokens)
			throws ProcessingException {
		var it = tokens.iterator();
		while (it.hasNext()) {
			var token = it.next();
			switch (token.hint()) {
				case IGNORE -> {
				}
				case INDENT -> {
					var nextToken = it.next();
					_processIndentToken(processor, token.token(), nextToken.token());
				}
				case NORMAL, EOF -> {
					processor.processToken(token.token());
				}
			}
		}

	}

	protected void beforeProcessToken(PositionedToken<MustacheToken> positionedToken) {
		this.position = positionedToken.position();
		this.currentToken = positionedToken;

	}

	protected void afterProcessToken(PositionedToken<MustacheToken> positionedToken) {
		if (positionedToken.innerToken().isNewlineOrEOF()) {
			atStartOfLine = true;
		}
		else {
			atStartOfLine = false;
		}
		this.lastProcessedToken = positionedToken;
	}

	protected final void _processToken(PositionedToken<MustacheToken> positionedToken) throws ProcessingException {
		beforeProcessToken(positionedToken);
		handleToken(positionedToken);
		afterProcessToken(positionedToken);
	}

	protected abstract void handleToken(PositionedToken<MustacheToken> positionedToken) throws ProcessingException;

	protected void _processIndentToken(MustacheTokenProcessor processor, PositionedToken<MustacheToken> whitespace,
			PositionedToken<MustacheToken> positionedToken) throws ProcessingException {
		if (positionedToken.innerToken().isIndented()) {
			if (whitespace.innerToken() instanceof TextToken tt) {
				if (isDebug()) {
					debug("Setting indent. whitespace: " + tt + " standalone: " + positionedToken.innerToken());
				}
				partialIndent = tt.text();
			}
			else {
				throw new IllegalStateException("whitespace token is wrong");
			}
		}
		processor.processToken(positionedToken);
	}

	protected record ProcessToken(PositionedToken<MustacheToken> token, ProcessHint hint) {

		protected static ProcessToken ignore(PositionedToken<MustacheToken> token) {
			if (token.innerToken().isEOF()) {
				return new ProcessToken(token, ProcessHint.EOF);
			}
			return new ProcessToken(token, ProcessHint.IGNORE);
		}

		protected static ProcessToken of(PositionedToken<MustacheToken> token) {
			if (token.innerToken().isEOF()) {
				return new ProcessToken(token, ProcessHint.EOF);
			}
			return new ProcessToken(token, ProcessHint.NORMAL);
		}

		protected static ProcessToken indent(PositionedToken<MustacheToken> token) {
			if (token.innerToken().isEOF()) {
				return new ProcessToken(token, ProcessHint.EOF);
			}
			return new ProcessToken(token, ProcessHint.INDENT);
		}

		public PositionedToken<MustacheToken> token() {
			return token;
		}

		protected enum ProcessHint {

			IGNORE, INDENT, NORMAL, EOF;

		}

	}

}
