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

import com.github.sviperll.staticmustache.token.MustacheTagKind;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public sealed interface MustacheToken {
    
    public record TagToken(MustacheTagKind tagKind, String name) implements MustacheToken {
        @Override
        public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
            return switch(tagKind()) {
            case BEGIN_SECTION -> visitor.beginSection(name);
            case BEGIN_BLOCK_SECTION -> visitor.beginBlockSection(name);
            case BEGIN_INVERTED_SECTION -> visitor.beginInvertedSection(name);
            case BEGIN_PARENT_SECTION -> visitor.beginParentSection(name);
            case END_SECTION -> visitor.endSection(name);
            case UNESCAPED_VARIABLE_THREE_BRACES -> visitor.unescapedVariable(name);
            case UNESCAPED_VARIABLE_TWO_BRACES -> visitor.unescapedVariable(name);
            case VARIABLE -> visitor.variable(name);
            case PARTIAL -> visitor.partial(name);

            };
        }
        public boolean isTagToken() {
            return true;
        };
        
        public boolean isSectionToken() {
            return tagKind().isSection();
        };
    }
    
    public record TextToken(String text) implements MustacheToken {
        @Override
        public <R, E extends Exception> R accept(MustacheToken.Visitor<R,E> visitor) throws E {
            return visitor.text(text);
        };
    }
    
    public record SpecialCharacterToken(char specialChar) implements MustacheToken {
        @Override
        public <R, E extends Exception> R accept(MustacheToken.Visitor<R,E> visitor) throws E {
            return visitor.specialCharacter(specialChar);
        };
    }
    
    public record NewlineToken(char newlineChar) implements MustacheToken {
        @Override
        public <R, E extends Exception> R accept(MustacheToken.Visitor<R,E> visitor) throws E {
            return visitor.newline(newlineChar);
        };
        
        @Override
        public boolean isNewlineToken() {
            return true;
        };
    }
    
    public record EndOfFileToken() implements MustacheToken {

        @Override
        public <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E {
            return visitor.endOfFile();
        }
        
    }
    
    /**
     * N.B this does not include newline!
     */
    default boolean isWhitespaceToken() {
        return isWhitespaceToken(this);
    }
    
    default boolean isTagToken() {
        return false;
    }
    
    default boolean isSectionToken() {
        return false;
    }
    
    default boolean isNewlineToken() {
        return false;
    }
    
    default boolean isNewlineOrEOF() {
        return isNewlineToken() || isEOF();
    }
    
    default boolean isEOF() {
        return this instanceof EndOfFileToken;
    }
    
    /**
     * N.B this does not include newline!
     */
    public static boolean isWhitespaceToken(MustacheToken token) {
        if (token instanceof TextToken tt) {
            return tt.text().isBlank();
        }
        return false;
    }
    

    public abstract <R, E extends Exception> R accept(Visitor<R, E> visitor) throws E;


    public interface Visitor<R, E extends Exception> {
        R beginSection(String name) throws E;
        R beginInvertedSection(String name) throws E;
        R beginParentSection(String name) throws E;
        R beginBlockSection(String name) throws E;
        R endSection(String name) throws E;
        R partial(String name) throws E;
        R variable(String name) throws E;
        R unescapedVariable(String name) throws E;
        R specialCharacter(char c) throws E;
        R newline(char c) throws E;
        R text(String s) throws E;
        R endOfFile() throws E;
    }
}
