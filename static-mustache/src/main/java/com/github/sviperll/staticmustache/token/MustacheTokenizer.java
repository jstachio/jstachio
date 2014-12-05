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
package com.github.sviperll.staticmustache.token;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class MustacheTokenizer implements TokenProcessor<PositionedToken<ParensisToken>> {
    public static MustacheTokenizer createInstance(TokenProcessor<PositionedToken<MustacheToken>> downstream) {
        return new MustacheTokenizer(new PositionHodingTokenProcessor<MustacheToken>(downstream));
    }

    private final PositionHodingTokenProcessor<MustacheToken> downstream;
    private State state = State.OUTSIDE;
    private StringBuilder text = new StringBuilder();
    private StringBuilder fieldName = new StringBuilder();
    public MustacheTokenizer(PositionHodingTokenProcessor<MustacheToken> downstream) {
        this.downstream = downstream;
    }
    @Override
    public void processToken(PositionedToken<ParensisToken> positionedToken) throws ProcessingException {
        final Position position = positionedToken.position();
        downstream.setPosition(position);
        ParensisToken token = positionedToken.innerToken();
        if (state == State.OUTSIDE) {
            token.accept(new ParensisToken.Visitor<Void, ProcessingException>() {
                @Override
                public Void openParensis() throws ProcessingException {
                    state = State.START;
                    flushText();
                    return null;
                }

                @Override
                public Void closingParensis() throws ProcessingException {
                    text.append("}}");
                    return null;
                }

                @Override
                public Void character(char c) throws ProcessingException {
                    if (c != '\n' && c != '"') {
                        text.append(c);
                    } else {
                        flushText();
                        downstream.processToken(MustacheToken.specialCharacter(c));
                    }
                    return null;
                }

                @Override
                public Void endOfFile() throws ProcessingException {
                    flushText();
                    downstream.processToken(MustacheToken.endOfFile());
                    return null;
                }

            });
        } else if (state == State.START) {
            token.accept(new ParensisToken.Visitor<Void, ProcessingException>() {
                @Override
                public Void openParensis() {
                    state = State.START;
                    return null;
                }

                @Override
                public Void closingParensis() throws ProcessingException {
                    throw new ProcessingException(position, "Unexpected closing parensis");
                }

                @Override
                public Void character(char c) throws ProcessingException {
                    if (Character.isWhitespace(c)) {
                    } else {
                        state = State.IDENTIFIER;
                        fieldName = new StringBuilder();
                        fieldName.append(c);
                    }
                    return null;
                }

                @Override
                public Void endOfFile() throws ProcessingException {
                    throw new ProcessingException(position, "Unclosed field at the end of file");
                }
            });
        } else if (state == State.IDENTIFIER) {
            token.accept(new ParensisToken.Visitor<Void, ProcessingException>() {
                @Override
                public Void openParensis() throws ProcessingException {
                    throw new ProcessingException(position, "Unexpected open parensis");
                }

                @Override
                public Void closingParensis() throws ProcessingException {
                    state = State.OUTSIDE;
                    downstream.processToken(MustacheToken.field(fieldName.toString()));
                    return null;
                }

                @Override
                public Void character(char c) throws ProcessingException {
                    if (Character.isWhitespace(c)) {
                        state = State.END;
                        downstream.processToken(MustacheToken.field(fieldName.toString()));
                    } else {
                        fieldName.append(c);
                    }
                    return null;
                }

                @Override
                public Void endOfFile() throws ProcessingException {
                    throw new ProcessingException(position, "Unclosed field at the end of file");
                }
            });
        } else if (state == State.END) {
            token.accept(new ParensisToken.Visitor<Void, ProcessingException>() {
                @Override
                public Void openParensis() throws ProcessingException {
                    throw new ProcessingException(position, "Unexpected open parensis");
                }

                @Override
                public Void closingParensis() throws ProcessingException {
                    state = State.OUTSIDE;
                    return null;
                }

                @Override
                public Void character(char c) throws ProcessingException {
                    if (!Character.isWhitespace(c)) {
                        throw new ProcessingException(position, "Unrecognized character " + c);
                    }
                    return null;
                }

                @Override
                public Void endOfFile() throws ProcessingException {
                    throw new ProcessingException(position, "Unclosed field at the end of file");
                }
            });
        } else
            throw new IllegalStateException("Unhandled state " + state);
    }

    private void flushText() throws ProcessingException {
        if (text.length() > 0) {
            downstream.processToken(MustacheToken.text(text.toString()));
            text = new StringBuilder();
        }
    }

    private enum State {OUTSIDE, START, IDENTIFIER, END};
}
