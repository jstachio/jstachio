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
package com.github.sviperll.staticmustache;

import com.github.sviperll.staticmustache.token.MustacheToken;
import com.github.sviperll.staticmustache.token.MustacheTokenizer;
import com.github.sviperll.staticmustache.token.ParansisTokenizer;
import com.github.sviperll.staticmustache.token.ProcessingException;
import com.github.sviperll.staticmustache.token.Position;
import com.github.sviperll.staticmustache.token.PositionAnnotator;
import com.github.sviperll.staticmustache.token.PositionedToken;
import com.github.sviperll.staticmustache.token.PositionedTransformer;
import com.github.sviperll.staticmustache.token.TokenProcessor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class TemplateCompiler implements TokenProcessor<PositionedToken<MustacheToken>> {
    private static boolean isJavaIdentifier(String name) {
        char[] chars = name.toCharArray();
        if (!Character.isJavaIdentifierStart(chars[0]))
            return false;
        else {
            for (int i = 1; i < chars.length; i++) {
                if (!Character.isJavaIdentifierPart(chars[0]))
                    return false;
            }
            return true;
        }
    }

    private final Reader inputReader;
    private final PrintWriter writer;
    private final TypeElementContext context;

    TemplateCompiler(Reader inputReader, PrintWriter writer, TypeElementContext context) {
        this.inputReader = inputReader;
        this.writer = writer;
        this.context = context;
    }

    public void run(String fileName) throws ProcessingException, IOException {
        MustacheTokenizer mustacheTokenizer = MustacheTokenizer.createInstance(this);
        TokenProcessor<PositionedToken<Character>> paransisTokenizer = PositionedTransformer.decorateTokenProcessor(ParansisTokenizer.decorator(), mustacheTokenizer);
        PositionAnnotator positionAnnotator = new PositionAnnotator(fileName, paransisTokenizer);
        int readResult;
        while ((readResult = inputReader.read()) >= 0) {
            positionAnnotator.processToken((char)readResult);
        }
        positionAnnotator.processToken(null); // Signal EOF
        writer.println();
    }

    public void append(String s) {
        writer.print("writer.append(\"" + s + "\"); ");
    }

    @Override
    public void processToken(PositionedToken<MustacheToken> positionedToken) throws ProcessingException {
        final Position position = positionedToken.position();
        MustacheToken token = positionedToken.innerToken();
        token.accept(new MustacheToken.Visitor<Void, ProcessingException>() {
            @Override
            public Void beginBlock(String name) throws ProcessingException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Void endBlock(String name) throws ProcessingException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Void field(String name) throws ProcessingException {
                if (!isJavaIdentifier(name))
                    throw new ProcessingException(position, "Wrong identifier: " + name);
                else {
                    try {
                        writer.print(context.inline(name));
                        return null;
                    } catch (TypeException ex) {
                        throw new ProcessingException(position, ex);
                    }
                }
            }

            @Override
            public Void specialCharacter(char c) throws ProcessingException {
                if (c == '\n') {
                    append("\\n");
                    writer.println();
                } else if (c == '"') {
                    append("\\\"");
                } else
                    append("" + c);
                return null;
            }

            @Override
            public Void text(String s) throws ProcessingException {
                append(s);
                return null;
            }

            @Override
            public Void endOfFile() throws ProcessingException {
                return null;
            }
        });
    }
}
