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
package com.snaphop.staticmustache.apt;

import java.io.IOException;

import org.jspecify.nullness.Nullable;

import com.github.sviperll.staticmustache.context.ContextException;
import com.github.sviperll.staticmustache.context.TemplateCompilerContext;
import com.github.sviperll.staticmustache.context.TemplateCompilerContext.ChildType;
import com.github.sviperll.staticmustache.token.MustacheTokenizer;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class TemplateCompiler implements TokenProcessor<PositionedToken<MustacheToken>> {
    public static Factory compilerFactory() {
        return new Factory() {
            @Override
            public TemplateCompiler createTemplateCompiler(NamedReader reader, SwitchablePrintWriter writer,
                                                           TemplateCompilerContext context) {
                return TemplateCompiler.createCompiler(reader, writer, context);
            }
        };
    }

    public static Factory headerCompilerFactory() {
        return new Factory() {
            @Override
            public TemplateCompiler createTemplateCompiler(NamedReader reader, SwitchablePrintWriter writer,
                                                           TemplateCompilerContext context) {
                return TemplateCompiler.createHeaderCompiler(reader, writer, context);
            }
        };
    }

    public static Factory footerCompilerFactory() {
        return new Factory() {
            @Override
            public TemplateCompiler createTemplateCompiler(NamedReader reader, SwitchablePrintWriter writer,
                                                           TemplateCompilerContext context) {
                return TemplateCompiler.createFooterCompiler(reader, writer, context);
            }
        };
    }

    public static TemplateCompiler createCompiler(NamedReader reader, SwitchablePrintWriter writer, TemplateCompilerContext context) {
        return new SimpleTemplateCompiler(reader, writer, context);
    }

    public static TemplateCompiler createHeaderCompiler(NamedReader reader, SwitchablePrintWriter writer, TemplateCompilerContext context) {
        return new HeaderTemplateCompiler(reader, writer, context);
    }

    public static TemplateCompiler createFooterCompiler(NamedReader reader, SwitchablePrintWriter writer, TemplateCompilerContext context) {
        return new FooterTemplateCompiler(reader, writer, context);
    }

    private final NamedReader reader;
    private final boolean expectsYield;
    final SwitchablePrintWriter writer;
    private TemplateCompilerContext context;
    boolean foundYield = false;
    int depth = 0;

    private TemplateCompiler(NamedReader reader, SwitchablePrintWriter writer, TemplateCompilerContext context, boolean expectsYield) {
        this.reader = reader;
        this.writer = writer;
        this.context = context;
        this.expectsYield = expectsYield;
    }

    void run() throws ProcessingException, IOException {
        TokenProcessor<Character> processor = MustacheTokenizer.createInstance(reader.name(), this);
        int readResult;
        while ((readResult = reader.read()) >= 0) {
            processor.processToken((char)readResult);
        }
        processor.processToken(TokenProcessor.EOF);
        writer.println();
    }

    @Override
    public void processToken(PositionedToken<MustacheToken> positionedToken) throws ProcessingException {
        positionedToken.innerToken().accept(new CompilingTokenProcessor(positionedToken.position()));
    }

    private class CompilingTokenProcessor implements MustacheToken.Visitor<@Nullable Void, ProcessingException> {
        private final Position position;

        public CompilingTokenProcessor(Position position) {
            this.position = position;
        }

        @Override
        public @Nullable Void beginSection(String name) throws ProcessingException {
            try {
                context = context.getChild(name, ChildType.SECTION);
                print("// section: " + context.currentEnclosedContextName());
                println();
                print(context.beginSectionRenderingCode());
                println();
                depth++;
                
            } catch (ContextException ex) {
                throw new ProcessingException(position, ex);
            }
            return null;
        }

        @Override
        public @Nullable Void beginInvertedSection(String name) throws ProcessingException {
            try {
                context = context.getChild(name, ChildType.INVERTED);
                print("// inverted section: " + context.currentEnclosedContextName());
                println();
                print(context.beginSectionRenderingCode());
                println();
                depth++;
            } catch (ContextException ex) {
                throw new ProcessingException(position, ex);
            }
            return null;
        }

        @Override
        public @Nullable Void endSection(String name) throws ProcessingException {
            if (!context.isEnclosed())
                throw new ProcessingException(position, "Closing " + name + " block when no block is currently open");
            else if (!context.currentEnclosedContextName().equals(name))
                throw new ProcessingException(position, "Closing " + name + " block instead of " + context.currentEnclosedContextName());
            else {
                depth--;
                print(context.endSectionRenderingCode());
                println();
                context = context.parentContext();
                return null;
            }
        }

        @Override
        public @Nullable Void variable(String name) throws ProcessingException {
            try {
                if (!expectsYield || !name.equals("yield")) {
                    //TemplateCompilerContext variable = context.getChild(name);
                    TemplateCompilerContext variable = context.getChild(name, ChildType.ESCAPED_VAR);
                    print("// variable: " + variable.currentEnclosedContextName());
                    println();
                    print(variable.renderingCode());
                    println();
                } else {
                    if (foundYield)
                        throw new ProcessingException(position, "Yield can be used only once");
                    else if (context.isEnclosed())
                        throw new ProcessingException(position, "Unclosed " + context.currentEnclosedContextName() + " block before yield");
                    else {
                        throw new ProcessingException(position, "Yield should be unescaped variable");
                    }
                }
                return null;
            } catch (ContextException ex) {
                throw new ProcessingException(position, ex);
            }
        }

        @Override
        public @Nullable Void unescapedVariable(String name) throws ProcessingException {
            try {
                if (!expectsYield || !name.equals("yield")) {
                    TemplateCompilerContext variable = context.getChild(name, ChildType.UNESCAPED_VAR);
                    print("// unescaped variable: " + variable.currentEnclosedContextName());
                    println();
                    print(variable.unescapedRenderingCode());
                    println();
                } else {
                    if (foundYield)
                        throw new ProcessingException(position, "Yield can be used only once");
                    if (context.isEnclosed())
                        throw new ProcessingException(position, "Unclosed " + context.currentEnclosedContextName() + " block before yield");
                    else {
                        foundYield = true;
                        if (writer.suppressesOutput())
                            writer.enableOutput();
                        else
                            writer.disableOutput();
                    }
                }
                return null;
            } catch (ContextException ex) {
                throw new ProcessingException(position, ex);
            }
        }

        @Override
        public @Nullable Void specialCharacter(char c) throws ProcessingException {
            if (c == '\n') {
                printCodeToWrite("\\n");
            } else if (c == '"') {
                printCodeToWrite("\\\"");
            } else
                printCodeToWrite("" + c);
            return null;
        }

        @Override
        public @Nullable Void text(String s) throws ProcessingException {
            printCodeToWrite(s);
            return null;
        }

        @Override
        public @Nullable Void endOfFile() throws ProcessingException {
            if (!context.isEnclosed())
                return null;
            else {
                throw new ProcessingException(position, "Unclosed " + context.currentEnclosedContextName() + " block at end of file");
            }
        }

        private void printCodeToWrite(String s) {
            print(context.unescapedWriterExpression() + ".append(\"" + s + "\"); ");
            println();
        }

        private void print(String s) {
            int i = 0;
            for (String line : s.split("\n")) {
                if (i > 0) {
                    println();
                }
                printIndent();
                writer.print(line);
                i++;
            }
        }
        
        private void printIndent() {
            for (int i = 0; i <= depth + 2; i++) {
                writer.print("    ");
            }
        }

        private void println() {
            writer.println();
        }

    }

    static class SimpleTemplateCompiler extends TemplateCompiler {
        private SimpleTemplateCompiler(NamedReader inputReader, SwitchablePrintWriter writer, TemplateCompilerContext context) {
            super(inputReader, writer, context, false);
        }

        @Override
        void run() throws ProcessingException, IOException {
            boolean suppressesOutput = writer.suppressesOutput();
            writer.enableOutput();
            super.run();
            if (suppressesOutput)
                writer.disableOutput();
            else
                writer.enableOutput();
        }
    }

    static class HeaderTemplateCompiler extends TemplateCompiler {
        private HeaderTemplateCompiler(NamedReader inputReader, SwitchablePrintWriter writer, TemplateCompilerContext context) {
            super(inputReader, writer, context, true);
        }

        @Override
        void run() throws ProcessingException, IOException {
            boolean suppressesOutput = writer.suppressesOutput();
            foundYield = false;
            writer.enableOutput();
            super.run();
            if (suppressesOutput)
                writer.disableOutput();
            else
                writer.enableOutput();
        }
    }

    static class FooterTemplateCompiler extends TemplateCompiler {
        private FooterTemplateCompiler(NamedReader inputReader, SwitchablePrintWriter writer, TemplateCompilerContext context) {
            super(inputReader, writer, context, true);
        }

        @Override
        void run() throws ProcessingException, IOException {
            boolean suppressesOutput = writer.suppressesOutput();
            foundYield = false;
            writer.disableOutput();
            super.run();
            if (suppressesOutput)
                writer.disableOutput();
            else
                writer.enableOutput();
        }
    }

    interface Factory {
        TemplateCompiler createTemplateCompiler(NamedReader reader, SwitchablePrintWriter writer, TemplateCompilerContext context);
    }
}
