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
 *     character materials provided with the distribution.
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
package com.github.sviperll.staticmustache.token.mustache;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public abstract class ParensisToken {
    public static ParensisToken openParensis() {
        return new ParensisToken() {
            @Override
            public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
                return visitor.openParensis();
            }
        };
    }
    public static ParensisToken closingParensis() {
        return new ParensisToken() {
            @Override
            public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
                return visitor.closingParensis();
            }
        };
    }
    public static ParensisToken character(final char s) {
        return new ParensisToken() {
            @Override
            public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
                return visitor.character(s);
            }
        };
    }
    public static ParensisToken endOfFile() {
        return new ParensisToken() {
            @Override
            public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
                return visitor.endOfFile();
            }
        };
    }
    public abstract <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E;
    private ParensisToken() {
    }

    public interface Visitor<R, E extends Exception> {
        R openParensis() throws E;
        R closingParensis() throws E;
        R character(char c) throws E;
        R endOfFile() throws E;
    }
}
