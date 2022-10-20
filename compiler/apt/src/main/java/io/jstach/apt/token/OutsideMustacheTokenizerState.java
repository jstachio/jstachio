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
package io.jstach.apt.token;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.MustacheToken;
import io.jstach.apt.MustacheToken.NewlineChar;
import io.jstach.apt.MustacheToken.SpecialChar;
import io.jstach.apt.ProcessingException;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class OutsideMustacheTokenizerState implements MustacheTokenizerState {
    private final StringBuilder text = new StringBuilder();
    private final MustacheTokenizer tokenizer;
    private char lastChar = 0;

    OutsideMustacheTokenizerState(final MustacheTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public @Nullable Void twoOpenBraces() throws ProcessingException {
        tokenizer.setState(new StartMustacheTokenizerState(tokenizer));
        return null;
    }

    @Override
    public @Nullable Void threeOpenBraces() throws ProcessingException {
        tokenizer.setState(new BeforeIdentifierMustacheTokenizerState(MustacheTagKind.UNESCAPED_VARIABLE_THREE_BRACES, tokenizer));
        return null;
    }

    @Override
    public @Nullable Void threeClosingBraces() throws ProcessingException {
        text.append("}}}");
        return null;
    }

    @Override
    public @Nullable Void twoClosingBraces() throws ProcessingException {
        text.append("}}");
        return null;
    }

    @Override
    public @Nullable Void character(char c) throws ProcessingException {
        switch (c) {
        case '\n' -> {
            tokenizer.setState(new OutsideMustacheTokenizerState(tokenizer));
            NewlineChar nc = lastChar == '\r' ? NewlineChar.CRLF : NewlineChar.LF;
            tokenizer.emitToken(new MustacheToken.NewlineToken(nc));
        }
        case '\r' -> {
            
        }
        case '"' -> {
            tokenizer.setState(new OutsideMustacheTokenizerState(tokenizer));
            tokenizer.emitToken(new MustacheToken.SpecialCharacterToken(SpecialChar.QUOTATION_MARK));
        }
        case '\\' -> {
            tokenizer.setState(new OutsideMustacheTokenizerState(tokenizer));
            tokenizer.emitToken(new MustacheToken.SpecialCharacterToken(SpecialChar.BACKSLASH));
        }
        default -> { 
            if (lastChar == '\r') {
                text.append("\r");
            }
            text.append(c);
        }
        }
        lastChar = c;
        return null;
    }

    @Override
    public @Nullable Void endOfFile() throws ProcessingException {
        tokenizer.setState(new OutsideMustacheTokenizerState(tokenizer));
        tokenizer.emitToken(new MustacheToken.EndOfFileToken());
        return null;
    }

    @Override
    public  void beforeStateChange() throws ProcessingException {
        if (text.length() > 0) {
            tokenizer.emitToken(new MustacheToken.TextToken(text.toString()));
        }
    }


}
