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
package com.github.sviperll.staticmustache.token.util;

import com.github.sviperll.staticmustache.token.BracesToken;
import com.snaphop.staticmustache.apt.PositionedToken;
import com.snaphop.staticmustache.apt.ProcessingException;
import com.snaphop.staticmustache.apt.TokenProcessor;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class BracesTokenizer implements TokenProcessor<Character> {
    static TokenProcessorDecorator<Character, BracesToken> decorator() {
        return new TokenProcessorDecorator<Character, BracesToken>() {
            @Override
            public TokenProcessor<Character> decorateTokenProcessor(TokenProcessor<BracesToken> downstream) {
                return new BracesTokenizer(downstream);
            }
        };
    }
    public static TokenProcessor<Character> createInstance(String fileName, TokenProcessor<PositionedToken<BracesToken>> downstream) {
        TokenProcessor<PositionedToken<Character>> paransisTokenizer = PositionedTransformer.decorateTokenProcessor(BracesTokenizer.decorator(), downstream);
        return new PositionAnnotator(fileName, paransisTokenizer);
    }

    private final TokenProcessor<BracesToken> downstream;
    private State state = State.NONE;

    public BracesTokenizer(TokenProcessor<BracesToken> downstream) {
        this.downstream = downstream;
    }

    @Override
    public void processToken(Character token) throws ProcessingException {
        if (token == null) {
            if (state == State.WAS_OPEN) {
                downstream.processToken(BracesToken.character('{'));
            } else if (state == State.WAS_OPEN_TWICE) {
                downstream.processToken(BracesToken.twoOpenBraces());
            } else if (state == State.WAS_CLOSE) {
                downstream.processToken(BracesToken.character('}'));
            } else if (state == State.WAS_CLOSE_TWICE) {
                downstream.processToken(BracesToken.twoClosingBraces());
            }
            downstream.processToken(BracesToken.endOfFile());
            state = State.NONE;
        } else if (token == '{') {
            if (state == State.WAS_OPEN) {
                state = State.WAS_OPEN_TWICE;
            } else if (state == State.WAS_OPEN_TWICE) {
                downstream.processToken(BracesToken.threeOpenBraces());
                state = State.NONE;
            } else if (state == State.WAS_CLOSE) {
                downstream.processToken(BracesToken.character('}'));
                state = State.WAS_OPEN;
            } else if (state == State.WAS_CLOSE_TWICE) {
                downstream.processToken(BracesToken.twoClosingBraces());
                state = State.WAS_OPEN;
            } else {
                state = State.WAS_OPEN;
            }
        } else if (token == '}') {
            if (state == State.WAS_CLOSE) {
                state = State.WAS_CLOSE_TWICE;
            } else if (state == State.WAS_CLOSE_TWICE) {
                downstream.processToken(BracesToken.threeClosingBraces());
                state = State.NONE;
            } else if (state == State.WAS_OPEN) {
                downstream.processToken(BracesToken.character('{'));
                state = State.WAS_CLOSE;
            } else if (state == State.WAS_OPEN_TWICE) {
                downstream.processToken(BracesToken.twoOpenBraces());
                state = State.WAS_CLOSE;
            } else {
                state = State.WAS_CLOSE;
            }
        } else {
            if (state == State.WAS_OPEN) {
                downstream.processToken(BracesToken.character('{'));
            } else if (state == State.WAS_OPEN_TWICE) {
                downstream.processToken(BracesToken.twoOpenBraces());
            } else if (state == State.WAS_CLOSE) {
                downstream.processToken(BracesToken.character('}'));
            } else if (state == State.WAS_CLOSE_TWICE) {
                downstream.processToken(BracesToken.twoClosingBraces());
            }
            downstream.processToken(BracesToken.character(token));
            state = State.NONE;
        }
    }

    private enum State {
        WAS_OPEN, WAS_OPEN_TWICE, WAS_CLOSE, WAS_CLOSE_TWICE, NONE
    }
}
