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
 *     text materials provided with the distribution.
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
package com.snaphop.staticmustache.apt;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public abstract class MustacheToken {
    public static MustacheToken beginSection(final String name) {
        return new MustacheToken() {
            @Override
            public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
                return visitor.beginSection(name);
            }
        };
    }
    public static MustacheToken beginInvertedSection(final String name) {
        return new MustacheToken() {
            @Override
            public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
                return visitor.beginInvertedSection(name);
            }
        };
    }
    public static MustacheToken beginParentSection(final String name) {
        return new MustacheToken() {
            @Override
            public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
                return visitor.beginParentSection(name);
            }
        };
    }
    public static MustacheToken beginBlockSection(final String name) {
        return new MustacheToken() {
            @Override
            public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
                return visitor.beginBlockSection(name);
            }
        };
    }
    public static MustacheToken endSection(final String name) {
        return new MustacheToken() {
            @Override
            public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
                return visitor.endSection(name);
            }
        };
    }
    public static MustacheToken variable(final String name) {
        return new MustacheToken() {
            @Override
            public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
                return visitor.variable(name);
            }
        };
    }
    public static MustacheToken unescapedVariable(final String name) {
        return new MustacheToken() {
            @Override
            public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
                return visitor.unescapedVariable(name);
            }
        };
    }
    public static MustacheToken specialCharacter(final char c) {
        return new MustacheToken() {
            @Override
            public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
                return visitor.specialCharacter(c);
            }
        };
    }
    public static MustacheToken text(final String s) {
        return new MustacheToken() {
            @Override
            public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
                return visitor.text(s);
            }
        };
    }
    public static MustacheToken endOfFile() {
        return new MustacheToken() {
            @Override
            public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
                return visitor.endOfFile();
            }
        };
    }

    public abstract <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E;

    private MustacheToken() {
    }
    public interface Visitor<R, E extends Exception> {
        R beginSection(String name) throws E;
        R beginInvertedSection(String name) throws E;
        R beginParentSection(String name) throws E;
        R beginBlockSection(String name) throws E;
        R endSection(String name) throws E;
        R variable(String name) throws E;
        R unescapedVariable(String name) throws E;
        R specialCharacter(char c) throws E;
        R text(String s) throws E;
        R endOfFile() throws E;
    }
}
