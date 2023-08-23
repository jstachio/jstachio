/*
 * Copyright (c) 2023, Adam Gent
 * Copyright (c) 2014, Victor Nazarov <asviraspossible@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation and/or
 *     other materials provided with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.jstach.apt.internal.token.util;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.PositionedToken;
import io.jstach.apt.internal.ProcessingException;
import io.jstach.apt.internal.TokenProcessor;
import io.jstach.apt.internal.token.BracesToken;
import io.jstach.apt.internal.token.BracesToken.TokenType;
import io.jstach.apt.internal.token.Delimiters;

public class BracesTokenizer implements TokenProcessor<@Nullable Character>, Delimiters.Subscriber {

	static TokenProcessorDecorator<@Nullable Character, BracesToken> decorator(Delimiters.Publisher publisher) {
		return new TokenProcessorDecorator<@Nullable Character, BracesToken>() {
			@Override
			public TokenProcessor<@Nullable Character> decorateTokenProcessor(TokenProcessor<BracesToken> downstream) {
				var bt = new BracesTokenizer(downstream);
				publisher.subscribe(bt);
				return bt;
			}
		};
	}

	public static TokenProcessor<@Nullable Character> createInstance(String fileName,
			TokenProcessor<PositionedToken<BracesToken>> downstream, Delimiters.Publisher publisher) {
		TokenProcessor<PositionedToken<@Nullable Character>> paransisTokenizer = PositionedTransformer
				.decorateTokenProcessor(BracesTokenizer.decorator(publisher), downstream);

		return new PositionAnnotator(fileName, paransisTokenizer);
	}

	private final TokenProcessor<BracesToken> downstream;

	private State state = State.NONE;

	private Delimiters delim = Delimiters.of();

	public BracesTokenizer(TokenProcessor<BracesToken> downstream) {
		this.downstream = downstream;
	}

	@Override
	public void setDelimters(Delimiters delimiters) {
		// TODO some validation of state here.
		this.delim = delimiters;

	}

	private void t(TokenType type) throws ProcessingException {
		switch (type) {
			case TWO_OPEN -> downstream.processToken(BracesToken.twoOpenBraces());
			case TWO_CLOSE -> downstream.processToken(BracesToken.twoClosingBraces());
			case THREE_OPEN -> downstream.processToken(BracesToken.threeOpenBraces());
			case THREE_CLOSE -> downstream.processToken(BracesToken.threeClosingBraces());
			case CHARACTER -> throw new IllegalStateException();
			case EOF -> throw new IllegalStateException();
			default -> throw new IllegalArgumentException("Unexpected value: " + type);
		}
	}

	private void c(char t) throws ProcessingException {
		downstream.processToken(BracesToken.character(t));
	}

	/*
	 * This is mainly for debugging purposes
	 */
	char lastChar;

	@Override
	public void processToken(@Nullable Character token) throws ProcessingException {

		/*
		 * We copy the delimiters as they can change during this method call. We then
		 * check to see if it has changed.
		 */
		Delimiters delim = this.delim;

		/*
		 * null is EOF
		 */
		if (token == null) {
			switch (state) {
				case NONE -> {
				}
				case WAS_OPEN -> {
					c(delim.start1());
				}
				case WAS_OPEN_TWICE -> {
					t(TokenType.TWO_OPEN);
				}
				case WAS_INSIDE -> {
				}
				case WAS_CLOSE -> {
					c(delim.end1());
				}
				case WAS_CLOSE_TWICE -> {
					t(TokenType.TWO_CLOSE);
				}
			}
			state = State.NONE;
			downstream.processToken(BracesToken.endOfFile());
			return;
		}
		/*
		 * What we do here for single delimiters that do not require a second delimiter we
		 * go ahead and make them TWICE_OPEN|CLOSE state when we see them.
		 */
		final char c = token;
		final State s = //
		//@formatter:off
		switch (state) {
			case NONE -> {
				if (delim.start1() == c) {
					if (delim.requiresStart2()) {
						yield State.WAS_OPEN;
					}
					else {
						yield State.WAS_OPEN_TWICE;
					}
				}
				else {
					c(c);
					yield State.NONE;
				}
			}
			case WAS_INSIDE -> {
				if (delim.end1() == c) {
					if (delim.requiresStart2()) {
						yield State.WAS_CLOSE;
					}
					else {
						yield State.WAS_CLOSE_TWICE;
					}
				}
				else {
					c(c);
					yield State.WAS_INSIDE;
				}
			}
			case WAS_OPEN -> {
				if (!delim.requiresStart2()) {
					throw new IllegalStateException();
				}
				else if (delim.start2() == c) {
					yield State.WAS_OPEN_TWICE;
				}
				else {
					c(delim.start1());
					c(c);
					yield State.NONE;
				}
			}
			case WAS_OPEN_TWICE -> {
				if (delim.start3() == c) {
					t(TokenType.THREE_OPEN);
					yield State.WAS_INSIDE;
				}
				else {
					t(TokenType.TWO_OPEN);
					c(c);
					yield State.WAS_INSIDE;
				}
			}
			case WAS_CLOSE -> {
				if (!delim.requiresEnd2()) {
					throw new IllegalStateException();
				}
				else if (delim.end2() == c) {
					yield State.WAS_CLOSE_TWICE;
				}
				else {
					c(delim.end1());
					c(c);
					yield State.NONE;
				}
			}
			case WAS_CLOSE_TWICE -> {
				if (delim.end3() == c) {
					t(TokenType.THREE_CLOSE);
					yield State.NONE;
				}
				else {
					t(TokenType.TWO_CLOSE);
					/*
					 * Here we update delimiter because it possible changed by the above
					 * two close token push.
					 */
					delim = this.delim;
					if (delim.start1() == c) {
						if (delim.requiresStart2()) {
							yield State.WAS_OPEN;
						}
						else {
							yield State.WAS_OPEN_TWICE;
						}
					}
					c(c);
					yield State.NONE;
				}
			}
		};
		//@formatter:on
		lastChar = c;
		state = s;
	}

	private enum State {

		WAS_OPEN, //
		WAS_OPEN_TWICE, //
		WAS_INSIDE, //
		WAS_CLOSE, //
		WAS_CLOSE_TWICE, //
		NONE

	}

}
