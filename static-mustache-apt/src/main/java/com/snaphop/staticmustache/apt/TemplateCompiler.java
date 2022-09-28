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
import com.snaphop.staticmustache.apt.CodeAppendable.HiddenCodeAppendable;
import com.snaphop.staticmustache.apt.CodeAppendable.StringCodeAppendable;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class TemplateCompiler implements TemplateCompilerLike, TokenProcessor<PositionedToken<MustacheToken>> {
    
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
        case PARAM_PARTIAL_TEMPLATE -> throw new IllegalArgumentException("Cannot create parent template as root");
        };
    }


    private final NamedReader reader;
    private final boolean expectsYield;
    private TemplateCompilerContext context;
    boolean foundYield = false;
    int depth = 0;
    StringBuilder currentUnescaped = new StringBuilder();
    private final TemplateCompilerLike parent;
    //TODO we probably need this as a stack as parent sections can include other parents or partials
    private @Nullable ParameterPartial _partial;
    //TODO fix content not in blocks in parent section
    protected @Nullable StringCodeAppendable _currentBlockOutput;
    
    protected @Nullable HiddenCodeAppendable _parentBlockOutput;
    
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
        currentWriter().println();
    }
    
    @Override
    public @Nullable TemplateCompilerLike getCaller() {
        return this.parent;
    }
    
    public @Nullable ParameterPartial currentParameterPartial() {
        return this._partial;
    }
    
    void popPartial() {
        this._partial = null;
    }
    
    void pushPartial(ParameterPartial partial) {
        this._partial = partial;
    }
    
    @Override
    public TemplateCompilerType getCompilerType() {
        return TemplateCompilerType.SIMPLE;
    }
    
    @Override
    public String getTemplateName() {
        return reader.name();
    }
    
    public CodeAppendable currentWriter() {
        if (_currentBlockOutput != null) {
            return _currentBlockOutput;
        }
        if (_parentBlockOutput != null) {
            return _parentBlockOutput;
        }
        return getWriter();
    }
    
    @Override
    public ParameterPartial createParameterPartial(String templateName) throws IOException {
        var reader = getTemplateLoader().open(templateName);
        TemplateCompilerContext context = this.context.createForPartial();
        var c = new TemplateCompiler(reader, this, context, expectsYield) {
            @Override
            public TemplateCompilerType getCompilerType() {
                return TemplateCompilerType.PARAM_PARTIAL_TEMPLATE;
            }
        };
        return new ParameterPartial(c);
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
        
        private void printBeginSectionComment() {
            println();
            print("// start " + context.getType() + ". name: " + context.currentEnclosedContextName() + ", template: " + getTemplateName());
            println();
        }
        
        private void printEndSectionComment() {
            println();
            print("// end " + context.getType() + ". name: " + context.currentEnclosedContextName() + ", template: " + getTemplateName());
            println();
        }

        @Override
        public @Nullable Void beginSection(String name) throws ProcessingException {
            flushUnescaped();
            var contextType = ChildType.SECTION;
            try {
                context = context.getChild(name, contextType);
                printBeginSectionComment();
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
            var contextType = ChildType.INVERTED;
            try {
                context = context.getChild(name, contextType);
                printBeginSectionComment();
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
            flushUnescaped();
            var contextType = ChildType.PARENT_PARTIAL;
            try {
                context = context.getChild(name, contextType);
                printBeginSectionComment();
                //We do not increase the printing depth
                //depth++;
                var p = currentParameterPartial();
                if (p != null) {
                    throw new IllegalStateException("partial is already started for this context");
                }
                p = createParameterPartial(name);
                pushPartial(p);
                _parentBlockOutput = HiddenCodeAppendable.INSTANCE;
                
            } catch (ContextException | IOException ex) {
                throw new ProcessingException(position, ex);
            }
            return null;
        }

        @Override
        public @Nullable Void beginBlockSection(String name) throws ProcessingException {
            flushUnescaped();
            var contextType = ChildType.BLOCK;
            try {
                context = context.getChild(name, contextType);
                printBeginSectionComment();
                // We do not increase the printing depth for blocks
                //depth++;
            } catch (ContextException e) {
                throw new ProcessingException(position, e);
            }
            var parameterPartial = currentParameterPartial();
            var caller = getCaller();
            if (parameterPartial != null) {
                /*
                 * {{< parent}}
                 * {{$block}} <-- We are here
                 * some content
                 * {{/block}} 
                 * {{/parent}}
                 */
                if (parameterPartial.getBlockArgs().containsKey(name)) {
                    throw new ProcessingException(position, "parameter block was defined earlier. block = " + name);
                }
                var writer = new StringCodeAppendable();
                parameterPartial.getBlockArgs().put(name, writer);
                if (_currentBlockOutput != null) {
                    throw new IllegalStateException("existing block output");
                }
                _currentBlockOutput = writer;
                if (currentWriter() != _currentBlockOutput) {
                    throw new IllegalStateException("unexpected current writer");
                }
                //println();
                print("// start BLOCK parameter. name: \"" + name + "\", template: " + getTemplateName() 
                + ", partial: " + parameterPartial.getTemplateName());
                println();
            }
            else if (caller != null) {
                /*
                 * We are in a block in a partial template
                 * e.g. partial.mustache
                 * {{$block}}{{/block}}
                 */
                  if (caller.currentParameterPartial() == null) {
                      throw new IllegalStateException("missing partial info");
                  }
                  if (_currentBlockOutput != null) {
                      throw new IllegalStateException("existing block output");
                  }
                  /*
                  * We will reconcile at the endSection if we actually need the output
                  */
                 _currentBlockOutput = new StringCodeAppendable();
                 //println();
                 print("// start BLOCK default. name: \"" + name + "\", template: " + getTemplateName());
                 println() ;
            }
            else {
                /*
                 * {{$block}}{{/block}}
                 */
                // Apparently this root template has block parameters
                // We do nothing for now
                //println();
                print("// unused block: " + name);
                println();
            }
            return null;
        }

        @Override
        public @Nullable Void endSection(String name) throws ProcessingException {

            flushUnescaped();
            if (!context.isEnclosed()) {
                throw new ProcessingException(position, "Closing " + name + " block when no block is currently open");
            }
            if (!context.currentEnclosedContextName().equals(name)) {
                throw new ProcessingException(position, "Closing " + name + " block instead of " + context.currentEnclosedContextName());
            }
            var contextType = context.getType();
            switch(contextType) {
            case PARENT_PARTIAL -> {
                /*
                 * We are at the end of a parent partial
                 * {{< parent}}
                 * {{/parent}} <-- we are here
                 */
                _parentBlockOutput = null;
                var p = currentParameterPartial();
                if (p == null) {
                    throw new IllegalStateException("partial is has not started for this context");
                }
                try (p) {
                    p.run();
                    popPartial();
                } catch (IOException e) {
                    throw new ProcessingException(position, e);
                }
            }
            case BLOCK -> {
                // Block END
                switch(getCompilerType()) {
                case PARAM_PARTIAL_TEMPLATE -> {
                    /*
                     * We are in a partial template at the end of a block
                     * {{$block}}
                     * {{/block}} <-- we are here
                     */
                    var callingTemplate = getCaller();
                    if (callingTemplate == null) {
                        throw new IllegalStateException("missing calling template");
                    }
                    StringCodeAppendable output = _currentBlockOutput;
                    if (output == null) {
                        throw new IllegalStateException("Missing block output");
                    }
                    
                    ParameterPartial callingPartial = callingTemplate.currentParameterPartial();
                    if (callingPartial == null) {
                        throw new IllegalStateException("missing partial info");
                    }
                    var callingBlock = callingPartial.getBlockArgs().get(name);
                    if (callingBlock != null) {
                        output = callingBlock;
                    }
                    _currentBlockOutput = null;
                    /*
                     * We dump the generated code to the
                     * class file being generated.
                     */
                    currentWriter().print(output.toString());
                    println();
                    if (callingBlock != null) {
                        print("// end BLOCK parameter. name: \"" + name + "\", template: " 
                                + callingTemplate.getTemplateName() 
                                + ", partial: " 
                                + callingPartial.getTemplateName());
                    }
                    else {
                        print("// end BLOCK default. name: \"" + name + "\", template: " 
                                + getTemplateName() 
                                + ", partial: "
                                + callingPartial.getTemplateName());
                    }
                    //println();
                }
                case HEADER,FOOTER,SIMPLE -> {
                    /*
                     * We are in the caller template at the end of a block
                     * {{$block}}
                     * {{/block}} <-- we are here
                     */
                    var p = currentParameterPartial();
                    if (p != null) {
                        /*
                         * We are inside of some {{< parent }}
                         * and the block is done so we can restore
                         * output
                         */
                        if (_currentBlockOutput == null) {
                            throw new IllegalStateException("should be capturing for the block");
                        }
                        if (_currentBlockOutput != p.getBlockArgs().get(name)) {
                            throw new IllegalStateException();
                        }
                       // println();
                       // print("// end calling block: " + name);
                       // println();
                        _currentBlockOutput = null;
                    }
                }
                }
            }
            case PATH, ESCAPED_VAR, UNESCAPED_VAR -> { throw new IllegalStateException("Context Type is wrong. " + context.getType());}
            case ROOT, SECTION, INVERTED -> {
                depth--;
            }
            };
            print(context.endSectionRenderingCode());
            printEndSectionComment();
            context = context.parentContext();
            return null;
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
                        if (currentWriter().suppressesOutput())
                            currentWriter().enableOutput();
                        else
                            currentWriter().disableOutput();
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
                currentWriter().print(line);
                i++;
            }
        }
        
        private void printIndent() {
            for (int i = 0; i <= depth + 2; i++) {
                currentWriter().print("    ");
            }
        }

        private void println() {
            currentWriter().println();
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
        public @Nullable TemplateCompilerLike getCaller() {
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
        
        @Override
        public TemplateCompilerType getCompilerType() {
            return TemplateCompilerType.HEADER;
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
        
        @Override
        public TemplateCompilerType getCompilerType() {
            return TemplateCompilerType.FOOTER;
        }
    }

    interface Factory {
        TemplateCompiler createTemplateCompiler(NamedReader reader, SwitchablePrintWriter writer, TemplateCompilerContext context);
    }
}
