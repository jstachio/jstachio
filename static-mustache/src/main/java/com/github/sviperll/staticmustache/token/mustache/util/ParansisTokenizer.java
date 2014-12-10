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
package com.github.sviperll.staticmustache.token.mustache.util;

import com.github.sviperll.staticmustache.token.mustache.ParensisToken;
import com.github.sviperll.staticmustache.token.PositionedToken;
import com.github.sviperll.staticmustache.token.ProcessingException;
import com.github.sviperll.staticmustache.token.TokenProcessor;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class ParansisTokenizer implements TokenProcessor<Character> {
    static TokenProcessorDecorator<Character, ParensisToken> decorator() {
        return new TokenProcessorDecorator<Character, ParensisToken>() {
            @Override
            public TokenProcessor<Character> decorateTokenProcessor(TokenProcessor<ParensisToken> downstream) {
                return new ParansisTokenizer(downstream);
            }
        };
    }
    public static TokenProcessor<Character> createInstance(String fileName, TokenProcessor<PositionedToken<ParensisToken>> downstream) {
        TokenProcessor<PositionedToken<Character>> paransisTokenizer = PositionedTransformer.decorateTokenProcessor(ParansisTokenizer.decorator(), downstream);
        return new PositionAnnotator(fileName, paransisTokenizer);
    }

    private final TokenProcessor<ParensisToken> downstream;
    private State state = State.NONE;

    public ParansisTokenizer(TokenProcessor<ParensisToken> downstream) {
        this.downstream = downstream;
    }

    @Override
    public void processToken(Character token) throws ProcessingException {
        if (token == null) {
            if (state == State.WAS_OPEN) {
                downstream.processToken(ParensisToken.character('{'));
            } else if (state == State.WAS_CLOSE) {
                downstream.processToken(ParensisToken.character('}'));
            }
            downstream.processToken(ParensisToken.endOfFile());
        } else if (token == '{') {
            if (state == State.WAS_OPEN) {
                state = State.NONE;
                downstream.processToken(ParensisToken.openParensis());
            } else if (state == State.WAS_CLOSE) {
                downstream.processToken(ParensisToken.character('}'));
                state = State.WAS_OPEN;
            } else {
                state = State.WAS_OPEN;
            }
        } else if (token == '}') {
            if (state == State.WAS_CLOSE) {
                state = State.NONE;
                downstream.processToken(ParensisToken.closingParensis());
            } else if (state == State.WAS_OPEN) {
                downstream.processToken(ParensisToken.character('{'));
                state = State.WAS_CLOSE;
            } else {
                state = State.WAS_CLOSE;
            }
        } else {
            if (state == State.WAS_OPEN) {
                downstream.processToken(ParensisToken.character('{'));
            } else if (state == State.WAS_CLOSE) {
                downstream.processToken(ParensisToken.character('}'));
            }
            downstream.processToken(ParensisToken.character(token));
        }
    }

    private enum State {
        WAS_OPEN, WAS_CLOSE, NONE
    }
}
