/*
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
package io.jstach.apt.internal.token;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.MustacheToken;
import io.jstach.apt.internal.Position;
import io.jstach.apt.internal.PositionedToken;
import io.jstach.apt.internal.ProcessingException;
import io.jstach.apt.internal.TokenProcessor;
import io.jstach.apt.internal.token.util.BracesTokenizer;
import io.jstach.apt.internal.token.util.PositionHodingTokenProcessor;

/**
 * @author Victor Nazarov
 */
public class MustacheTokenizer implements TokenProcessor<PositionedToken<BracesToken>> {

	/**
	 * Creates TokenProcessor to be feed one-by-one with each character of mustache
	 * template. Last fed character must be TokenProcessor#EOF wich denotes end of file.
	 * @param fileName fileName used in error messages. It can be custom string like
	 * "&lt;stdin&gt;"
	 * @param downstream TokenProcessor is invoked on each found MustacheToken
	 * @return .
	 */
	public static TokenProcessor<@Nullable Character> createInstance(String fileName,
			TokenProcessor<PositionedToken<MustacheToken>> downstream) {
		MustacheTokenizer mustacheTokenizer = new MustacheTokenizer(
				new PositionHodingTokenProcessor<MustacheToken>(downstream));
		var publisher = mustacheTokenizer.getDelimitersPublisher();
		return BracesTokenizer.createInstance(fileName, mustacheTokenizer, publisher);
	}

	private final PositionHodingTokenProcessor<MustacheToken> downstream;

	private MustacheTokenizerState state = new OutsideMustacheTokenizerState(this);

	private Position position = Position.noPosition();

	private Delimiters.Publisher delimitersPublisher = new Delimiters.Publisher();

	MustacheTokenizer(PositionHodingTokenProcessor<MustacheToken> downstream) {
		this.downstream = downstream;
	}

	@Override
	public void processToken(PositionedToken<BracesToken> positionedToken) throws ProcessingException {
		var p = position = positionedToken.position();
		downstream.setPosition(p);
		BracesToken token = positionedToken.innerToken();
		token.accept(state);
	}

	public Delimiters.Publisher getDelimitersPublisher() {
		return delimitersPublisher;
	}

	void setState(MustacheTokenizerState newState) throws ProcessingException {
		state.beforeStateChange();
		state = newState;
	}

	void error(String message) throws ProcessingException {
		throw new ProcessingException(position, message);
	}

	void emitToken(MustacheToken token) throws ProcessingException {
		downstream.processToken(token);
	}

}
