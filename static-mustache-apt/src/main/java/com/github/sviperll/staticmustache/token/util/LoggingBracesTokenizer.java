/*
 * Copyright (c) 2014, vir
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
import com.snaphop.staticmustache.apt.ProcessingException;
import com.snaphop.staticmustache.apt.TokenProcessor;

/**
 *
 * @author vir
 */
public class LoggingBracesTokenizer implements TokenProcessor<BracesToken>, BracesToken.Visitor<Void, RuntimeException> {
    private final TokenProcessor<BracesToken> downstream;

    public LoggingBracesTokenizer(TokenProcessor<BracesToken> downstream) {
        this.downstream = downstream;
    }

    @Override
    public void processToken(BracesToken token) throws ProcessingException {
        token.accept(this);
        downstream.processToken(token);
    }

    @Override
    public Void twoOpenBraces() throws RuntimeException {
        System.err.println("twoOpenBraces");
        return null;
    }

    @Override
    public Void twoClosingBraces() throws RuntimeException {
        System.err.println("twoClosingBraces");
        return null;
    }

    @Override
    public Void threeOpenBraces() throws RuntimeException {
        System.err.println("threeOpenBraces");
        return null;
    }

    @Override
    public Void threeClosingBraces() throws RuntimeException {
        System.err.println("threeClosingBraces");
        return null;
    }

    @Override
    public Void character(char c) throws RuntimeException {
        System.err.println("character: " + c);
        return null;
    }

    @Override
    public Void endOfFile() throws RuntimeException {
        System.err.println("endOfFile");
        return null;
    }

}
