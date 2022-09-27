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

import org.eclipse.jdt.annotation.Nullable;

import com.github.sviperll.staticmustache.context.ContextException;
import com.github.sviperll.staticmustache.context.TemplateCompilerContext;
import com.github.sviperll.staticmustache.context.TemplateCompilerContext.ChildType;
import com.github.sviperll.staticmustache.token.MustacheTokenizer;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class TemplateCompiler implements TemplateCompilerLike, TokenProcessor<PositionedToken<MustacheToken>> {
    
    public enum TemplateCompilerType {
        SIMPLE,
        HEADER,
        FOOTER
    }
    public static TemplateCompiler createCompiler(                
            String templateName,
            TemplateLoader templateLoader,
            CodeAppendable writer,
            TemplateCompilerContext context,
            TemplateCompilerType compilerType) throws IOException {
        
       return switch (compilerType) {
        case FOOTER -> new FooterTemplateCompiler(templateName, templateLoader, writer, context);
        case HEADER -> new HeaderTemplateCompiler(templateName, templateLoader, writer, context);
        case SIMPLE -> new SimpleTemplateCompiler(templateName, templateLoader, writer, context);
        };
    }


    private final NamedReader reader;
    private final boolean expectsYield;
    private TemplateCompilerContext context;
    boolean foundYield = false;
    int depth = 0;
    StringBuilder currentUnescaped = new StringBuilder();
    private final TemplateCompilerLike parent;
    private @Nullable PartialTemplateCompiler partial;
    //Map<String,String> blockArgs

    private TemplateCompiler(NamedReader reader, 
            TemplateCompilerLike parent, 
            TemplateCompilerContext context,
            boolean expectsYield) {
        this.reader = reader;
        this.parent = parent;
        this.context = context;
        this.expectsYield = expectsYield;
    }

    public void run() throws ProcessingException, IOException {
        TokenProcessor<Character> processor = MustacheTokenizer.createInstance(reader.name(), this);
        int readResult;
        while ((readResult = reader.read()) >= 0) {
            processor.processToken((char)readResult);
        }
        processor.processToken(TokenProcessor.EOF);
        getWriter().println();
    }
    
    @Override
    public @Nullable TemplateCompilerLike getParent() {
        return this.parent;
    }
    
    @Override
    public PartialTemplateCompiler createPartialCompiler(String templateName) throws IOException {
        var reader = getTemplateLoader().open(templateName);
        TemplateCompilerContext context = this.context.createForPartial();
        var c = new TemplateCompiler(reader, this, context, expectsYield);
        return new PartialTemplateCompiler(c);
    }

    @Override
    public void processToken(PositionedToken<MustacheToken> positionedToken) throws ProcessingException {
        positionedToken.innerToken().accept(new CompilingTokenProcessor(positionedToken.position()));
    }
    
    @Override
    public void close() throws IOException {
        reader.close();
    }

    private class CompilingTokenProcessor implements MustacheToken.Visitor<@Nullable Void, ProcessingException> {
        private final Position position;

        public CompilingTokenProcessor(Position position) {
            this.position = position;
        }
        
        void flushUnescaped() {
            var code = currentUnescaped.toString();
            if (! code.isEmpty()) {
                _printCodeToWrite(code);
            }
            currentUnescaped.setLength(0);
        }

        @Override
        public @Nullable Void beginSection(String name) throws ProcessingException {
            flushUnescaped();
            try {
                context = context.getChild(name, ChildType.SECTION);
                println();
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
            flushUnescaped();
            try {
                context = context.getChild(name, ChildType.INVERTED);
                println();
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
        public @Nullable Void beginParentSection(String name) throws ProcessingException {
            System.out.println("beginParent: " + name);

            flushUnescaped();
            try {
                context = context.getChild(name, ChildType.PARENT);
                println();
                print("// section: " + context.currentEnclosedContextName());
                println();
                //print(context.beginSectionRenderingCode());
                //println()
                depth++;
                if (partial != null) {
                    throw new IllegalStateException("partial is already started for this context");
                }
                partial = createPartialCompiler(name);
                
            } catch (ContextException | IOException ex) {
                throw new ProcessingException(position, ex);
            }
            return null;
        }

        @Override
        public @Nullable Void beginBlockSection(String name) throws ProcessingException {
            // TODO Auto-generated method stub
            return null;
        }


        @Override
        public @Nullable Void endSection(String name) throws ProcessingException {

            flushUnescaped();
            if (!context.isEnclosed()) {
                throw new ProcessingException(position, "Closing " + name + " block when no block is currently open");
            }
            else if (!context.currentEnclosedContextName().equals(name)) {
                throw new ProcessingException(position, "Closing " + name + " block instead of " + context.currentEnclosedContextName());
            }
            else {
                if (context.getType() == ChildType.PARENT) {
                    var p = partial;
                    if (p == null) {
                        throw new IllegalStateException("partial is already started for this context");
                    }
                    try {
                        p.run();
                        partial = null;
                    } catch (IOException e) {
                        throw new ProcessingException(position, e);
                    }
                    
                }
                depth--;
                print(context.endSectionRenderingCode());
                println();
                print("// end section: " + context.currentEnclosedContextName());
                println();
                context = context.parentContext();
                return null;
            }
        }

        @Override
        public @Nullable Void variable(String name) throws ProcessingException {
            flushUnescaped();
            println();
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
            flushUnescaped();
            println();
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
                        if (getWriter().suppressesOutput())
                            getWriter().enableOutput();
                        else
                            getWriter().disableOutput();
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
            flushUnescaped();
            if (!context.isEnclosed())
                return null;
            else {
                throw new ProcessingException(position, "Unclosed " + context.currentEnclosedContextName() + " block at end of file");
            }
        }

        private void printCodeToWrite(String s) {
            currentUnescaped.append(s);
        }
        
        
        private void _printCodeToWrite(String s) {
            if (s.isEmpty()) return;
            int i = 0;
            StringBuilder code = new StringBuilder();
            for (String line : CodeNewLineSplitter.split(s, "\\n")) {
                if (i > 0) {
                    code.append(" +");
                }
                code.append("\n    \"");
                code.append(line);
                code.append("\"");
                i++;
            }
            println();
            print(context.unescapedWriterExpression() + ".append(" + code.toString() + "); ");
            println();
        }

        private void print(String s) {
            int i = 0;
            for (String line : s.split("\n")) {
                if (i > 0) {
                    println();
                }
                printIndent();
                getWriter().print(line);
                i++;
            }
        }
        
        private void printIndent() {
            for (int i = 0; i <= depth + 2; i++) {
                getWriter().print("    ");
            }
        }

        private void println() {
            getWriter().println();
        }

    }

    static class RootTemplateCompiler extends TemplateCompiler {
        
        private final TemplateLoader templateLoader;
        private final CodeAppendable writer;
        
        public RootTemplateCompiler(
                String templateName,
                TemplateLoader templateLoader,
                CodeAppendable writer,
                TemplateCompilerContext context, 
                boolean expectsYield) throws IOException {
            super(templateLoader.open(templateName), null, context, expectsYield);
            this.templateLoader = templateLoader;
            this.writer = writer;
        }

        @Override
        public @Nullable TemplateCompilerLike getParent() {
            return null;
        }
        
        @Override
        public TemplateLoader getTemplateLoader() {
            return this.templateLoader;
        }
        
        @Override
        public CodeAppendable getWriter() {
            return this.writer;
        }
        
    }
    
    static class SimpleTemplateCompiler extends RootTemplateCompiler {
        
        private SimpleTemplateCompiler(String templateName,
                TemplateLoader templateLoader,
                CodeAppendable writer,
                TemplateCompilerContext context) throws IOException {
            super(templateName, templateLoader, writer, context, false);
        }

        @Override
        public void run() throws ProcessingException, IOException {
            boolean suppressesOutput = getWriter().suppressesOutput();
            getWriter().enableOutput();
            super.run();
            if (suppressesOutput)
                getWriter().disableOutput();
            else
                getWriter().enableOutput();
        }
    }

    static class HeaderTemplateCompiler extends RootTemplateCompiler {
        private HeaderTemplateCompiler(
                String templateName,
                TemplateLoader templateLoader,
                CodeAppendable writer,
                TemplateCompilerContext context
                ) throws IOException {
            super(templateName, templateLoader, writer, context, true);

        }

        @Override
        public void run() throws ProcessingException, IOException {
            boolean suppressesOutput = getWriter().suppressesOutput();
            foundYield = false;
            getWriter().enableOutput();
            super.run();
            if (suppressesOutput)
                getWriter().disableOutput();
            else
                getWriter().enableOutput();
        }
    }

    static class FooterTemplateCompiler extends RootTemplateCompiler {
        private FooterTemplateCompiler(
                String templateName,
                TemplateLoader templateLoader,
                CodeAppendable writer,
                TemplateCompilerContext context
                ) throws IOException {
            super(templateName, templateLoader, writer, context, true);
        }

        @Override
        public void run() throws ProcessingException, IOException {
            boolean suppressesOutput = getWriter().suppressesOutput();
            foundYield = false;
            getWriter().disableOutput();
            super.run();
            if (suppressesOutput)
                getWriter().disableOutput();
            else
                getWriter().enableOutput();
        }
    }

    interface Factory {
        TemplateCompiler createTemplateCompiler(NamedReader reader, SwitchablePrintWriter writer, TemplateCompilerContext context);
    }
}
