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

import com.github.sviperll.staticmustache.context.ContextException;
import com.github.sviperll.staticmustache.context.TemplateCompilerContext;
import com.github.sviperll.staticmustache.token.MustacheTokenizer;
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
    private TemplateCompilerContext context;

    TemplateCompiler(Reader inputReader, PrintWriter writer, TemplateCompilerContext context) {
        this.inputReader = inputReader;
        this.writer = writer;
        this.context = context;
    }

    public void run(String fileName) throws ProcessingException, IOException {
        TokenProcessor<Character> processor = MustacheTokenizer.createInstance(fileName, this);
        int readResult;
        while ((readResult = inputReader.read()) >= 0) {
            processor.processToken((char)readResult);
        }
        processor.processToken(TokenProcessor.EOF);
        writer.println();
    }

    public void append(String s) {
        writer.print(context.textRenderingCode(s));
    }

    @Override
    public void processToken(PositionedToken<MustacheToken> positionedToken) throws ProcessingException {
        positionedToken.innerToken().accept(new CompilingTokenProcessor(positionedToken.position()));
    }

    private class CompilingTokenProcessor implements MustacheToken.Visitor<Void, ProcessingException> {
        private final Position position;

        public CompilingTokenProcessor(Position position) {
            this.position = position;
        }

        @Override
        public Void beginSection(String name) throws ProcessingException {
            try {
                context = context.getChild(name);
                writer.print(context.beginSectionRenderingCode());
            } catch (ContextException ex) {
                throw new ProcessingException(position, ex);
            }
            return null;
        }

        @Override
        public Void beginInvertedSection(String name) throws ProcessingException {
            try {
                context = context.getInvertedChild(name);
                writer.print(context.beginSectionRenderingCode());
            } catch (ContextException ex) {
                throw new ProcessingException(position, ex);
            }
            return null;
        }

        @Override
        public Void endSection(String name) throws ProcessingException {
            if (!context.isEnclosed())
                throw new ProcessingException(position, "Closing " + name + " block when no block is currently open");
            else if (!context.currentEnclosedContextName().equals(name))
                throw new ProcessingException(position, "Closing " + name + " block instead of " + context.currentEnclosedContextName());
            else {
                writer.print(context.endSectionRenderingCode());
                context = context.parentContext();
                return null;
            }
        }

        @Override
        public Void variable(String name) throws ProcessingException {
            try {
                TemplateCompilerContext variable = context.getChild(name);
                writer.print(variable.renderingCode());
                return null;
            } catch (ContextException ex) {
                throw new ProcessingException(position, ex);
            }
        }

        @Override
        public Void unescapedVariable(String name) throws ProcessingException {
            try {
                TemplateCompilerContext variable = context.getChild(name);
                writer.print(variable.unescapedRenderingCode());
                return null;
            } catch (ContextException ex) {
                throw new ProcessingException(position, ex);
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
    }
}
