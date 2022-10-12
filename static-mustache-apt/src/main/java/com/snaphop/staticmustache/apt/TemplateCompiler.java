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
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import com.github.sviperll.staticmustache.TemplateCompilerFlags;
import com.github.sviperll.staticmustache.context.ContextException;
import com.github.sviperll.staticmustache.context.TemplateCompilerContext;
import com.github.sviperll.staticmustache.context.TemplateCompilerContext.ContextType;
import com.github.sviperll.staticmustache.token.MustacheTagKind;
import com.github.sviperll.staticmustache.token.MustacheTokenizer;
import com.snaphop.staticmustache.apt.CodeAppendable.HiddenCodeAppendable;
import com.snaphop.staticmustache.apt.CodeAppendable.StringCodeAppendable;
import com.snaphop.staticmustache.apt.MustacheToken.NewlineChar;
import com.snaphop.staticmustache.apt.MustacheToken.SpecialChar;
import com.snaphop.staticmustache.apt.MustacheToken.TagToken;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class TemplateCompiler extends AbstractTemplateCompiler {
    
    public static TemplateCompiler createCompiler(
            String templateName,
            TemplateLoader templateLoader,
            CodeAppendable writer,
            TemplateCompilerContext context,
            TemplateCompilerType compilerType,
            Set<TemplateCompilerFlags.Flag> flags
            ) throws IOException {
        
       return switch (compilerType) {
        case FOOTER -> new FooterTemplateCompiler(templateName, templateLoader, writer, context);
        case HEADER -> new HeaderTemplateCompiler(templateName, templateLoader, writer, context);
        case SIMPLE -> new SimpleTemplateCompiler(templateName, templateLoader, writer, context, flags);
        case PARTIAL_TEMPLATE, PARAM_PARTIAL_TEMPLATE -> throw new IllegalArgumentException("Cannot create partial template as root");
        };
    }

    private final NamedReader reader;
    private final boolean expectsYield;
    private TemplateCompilerContext context;
    boolean foundYield = false;
    int depth = 0;
    StringBuilder currentUnescaped = new StringBuilder();
    String indent = "";
    
    private final TemplateCompilerLike parent;
    
    private @Nullable ParameterPartial _partial;

    protected @Nullable StringCodeAppendable _currentBlockOutput;
    
    protected @Nullable HiddenCodeAppendable _parentBlockOutput;

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
    protected void processTokenGroup(List<ProcessToken> tokens) throws ProcessingException {
        if (inLambda()) {
            processInsideLambdaToken(tokens);
        }
        else {
            super.processTokenGroup(tokens);
        }
    }
    
    void processInsideLambdaToken(List<ProcessToken> tokens) throws ProcessingException {
        String lambdaName = context.currentEnclosedContextName();
        for (var t : tokens) {
            var mt = t.token().innerToken();
            if (mt instanceof TagToken tt 
                    && tt.tagKind() == MustacheTagKind.END_SECTION 
                    && lambdaName.equals(tt.name())) {
                super._processToken(t.token());
            }
            else if (mt.isEOF()) {
                throw new ProcessingException(position, "EOF reached before lambda closing tag found. lambda = " + lambdaName);
            }
            else {
                mt.appendEscapedJava(currentUnescaped);
            }
        }
        
        
    }
    
    
    protected boolean inLambda() {
       return context.getType() == ContextType.LAMBDA;
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
        TemplateCompilerContext context = this.context.createForParameterPartial(templateName);
        var c = new TemplateCompiler(reader, this, context, expectsYield) {
            @Override
            public TemplateCompilerType getCompilerType() {
                return TemplateCompilerType.PARAM_PARTIAL_TEMPLATE;
            }
        };
        c.indent = partialIndent;
        partialIndent = "";
        return new ParameterPartial(c);
    }
    
    
    public Partial createPartial(String templateName) throws IOException {
        var reader = getTemplateLoader().open(templateName);
        TemplateCompilerContext context = this.context.createForPartial(templateName);
        var c = new TemplateCompiler(reader, this, context, expectsYield) {
            @Override
            public TemplateCompilerType getCompilerType() {
                return TemplateCompilerType.PARTIAL_TEMPLATE;
            }
        };
        c.indent = partialIndent;
        partialIndent = "";
        return new Partial(c);
    }

    
    void flushUnescaped() {
        var code = currentUnescaped.toString();
        if (! code.isEmpty()) {
            _printCodeToWrite(code);
        }
        currentUnescaped.setLength(0);
    }
    
    private void printCodeToWrite(String s) {
        currentUnescaped.append(s);
    }
    
    
    private void _printCodeToWrite(String s) {
        if (s.isEmpty()) return;
        String code = stringLiteralConcat(s);
        println();
        print(context.unescapedWriterExpression() + ".append(" + code + "); ");
        println();
    }

    private String stringLiteralConcat(String s) {
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
        String result = code.toString();
        if (result.isEmpty()) {
            result = "\"\"";
        }
        return result;
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
    protected void _beginSection(String name) throws ProcessingException {
        flushUnescaped();
        var contextType = ContextType.SECTION;
        try {
            context = context.getChild(name, contextType);
            printBeginSectionComment();
            print(context.beginSectionRenderingCode());
            println();
            depth++;
            /*
             * See if the context type is now a lambda
             */
            if (context.getType() == ContextType.LAMBDA) {
                _beginLambdaSection(name);
            }
            
        } catch (ContextException ex) {
            throw new ProcessingException(position, ex);
        }
    }
    
    protected void _beginLambdaSection(String name) {
        if (isDebug()) {
            debug("Begin lambda. name = " + name);
        }
    }
    
    protected void _endLambdaSection(String name) throws ProcessingException {
        if (isDebug()) {
            debug("End Lambda. name = " + name);
        }
        try {
            String code = stringLiteralConcat(currentUnescaped.toString());
            currentUnescaped.setLength(0);
            print(context.lambdaRenderingCode(code));
        } catch (ContextException ex) {
            throw new ProcessingException(position, ex);
        }
    }
    
    @Override
    protected void _beginInvertedSection(String name) throws ProcessingException {
        flushUnescaped();
        var contextType = ContextType.INVERTED;
        try {
            context = context.getChild(name, contextType);
            printBeginSectionComment();
            print(context.beginSectionRenderingCode());
            println();
            depth++;
        } catch (ContextException ex) {
            throw new ProcessingException(position, ex);
        }
    }
    
    @Override
    protected void _beginParentSection(String name) throws ProcessingException {
        flushUnescaped();
        var contextType = ContextType.PARENT_PARTIAL;
        try {
            context = context.getChild(name, contextType);
            printBeginSectionComment();
            //We do not increase the printing depth
            //depth++;
            var p = currentParameterPartial();
            if (p != null) {
                throw new IllegalStateException("parent (parameter partial) is already started for this context");
            }
            p = createParameterPartial(name);
            pushPartial(p);
            _parentBlockOutput = new HiddenCodeAppendable(s -> {
                /* if (isDebug()) { debug(s);} */
            } ); 
            
        } catch (ContextException | IOException ex) {
            throw new ProcessingException(position, ex);
        }
    }
    
    @Override
    protected void _beginBlockSection(String name) throws ProcessingException {
        flushUnescaped();
        var contextType = ContextType.BLOCK;
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
        
        var templateType = getCompilerType();
        
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
                throw new IllegalStateException("existing block output. template: " + getTemplateName());
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
        else if (templateType == TemplateCompilerType.PARAM_PARTIAL_TEMPLATE && caller != null) {
            /*
             * We are in a block in a partial template
             * e.g. partial.mustache
             * {{$block}}{{/block}}
             */
              if (getCompilerType() == TemplateCompilerType.PARAM_PARTIAL_TEMPLATE && 
                      caller.currentParameterPartial() == null) {
                  throw new IllegalStateException("bug. missing partial parameter info");
              }
              if (_currentBlockOutput != null) {
                  throw new IllegalStateException("existing block output. template: " + getTemplateName() + " name: " + name);
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
            // Apparently this either a root or partial template has block parameters.
            // We do nothing for now
            //println();
            print("// unused block: " + name);
            println();
        }
    }
    
    @Override
    protected void _endSection(String name) throws ProcessingException {
        if (!context.isEnclosed()) {
            throw new ProcessingException(position, "Closing " + name + " block when no block is currently open");
        }
        if (!context.currentEnclosedContextName().equals(name)) {
            throw new ProcessingException(position, "Closing " + name + " block instead of " + context.currentEnclosedContextName());
        }
        var contextType = context.getType();
        switch(contextType) {
        case LAMBDA -> {
            _endLambdaSection(name);
            depth--;
        }
        case PARENT_PARTIAL -> {
            flushUnescaped();
            _endParentSection(name);
        }
        case BLOCK -> {
            flushUnescaped();
            _endBlockSection(name);
        }
        case PATH, ESCAPED_VAR, UNESCAPED_VAR, PARTIAL -> { throw new IllegalStateException("Context Type is wrong. " + context.getType());}
        case ROOT, SECTION, INVERTED -> {
            flushUnescaped();
            depth--;
        }
        };
        print(context.endSectionRenderingCode());
        printEndSectionComment();
        context = context.parentContext();
    }

    private void _endParentSection(String name) throws ProcessingException {
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
            if (isDebug()) {
                debug("Running partial. " + p);
            }
            p.run();
            popPartial();
        } catch (IOException e) {
            throw new ProcessingException(position, e);
        }
    }

    private void _endBlockSection(String name) {
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
            var callingBlock =  callingPartial.findBlock(name); //callingPartial.getBlockArgs().get(name);
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
        }
        case HEADER,FOOTER,SIMPLE,PARTIAL_TEMPLATE-> {
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
                _currentBlockOutput = null;
            }
        }
        }
    }
    
    @Override
    protected void _variable(String name) throws ProcessingException {
        indent();
        flushUnescaped();
        println();
        try {
            if (!expectsYield || !name.equals("yield")) {
                //TODO figure out indenting variables
                TemplateCompilerContext variable = context.getChild(name, ContextType.ESCAPED_VAR);
                print("// variable: " + variable.currentEnclosedContextName());
                println();
                print(variable.renderingCode());
                println();
            } else {
                if (foundYield)
                    throw new ProcessingException(position, "Yield can be used only once");
                else if (context.isEnclosed())
                    throw new ProcessingException(position, "Unclosed \"" + context.currentEnclosedContextName() + "\" block before yield");
                else {
                    throw new ProcessingException(position, "Yield should be unescaped variable");
                }
            }
        } catch (ContextException ex) {
            var templateStack = context.getTemplateStack();
            System.out.println("Variable not found." + " var: " + name +  ", template: " +  templateStack.describeTemplateStack() + " context stack: " + context.printStack() + "\n");
            throw new ProcessingException(position, ex);
        }
    }
    
    @Override
    protected void _partial(String name) throws ProcessingException {
        flushUnescaped();
        println();
        var contextType = ContextType.PARTIAL;
        try {
            context = context.getChild(name, contextType);
            printBeginSectionComment();
            //We do not increase the printing depth
            //depth++;
            var pp = currentParameterPartial();
            if (pp != null) {
                throw new IllegalStateException("parent (parameter partial) is already started for this context");
            }
            try (var p = createPartial(name)) {
                p.run();
            }
            
        } catch (ContextException | IOException ex) {
            throw new ProcessingException(position, ex);
        }
        print(context.endSectionRenderingCode());
        printEndSectionComment();
        context = context.parentContext();
    }
    
    @Override
    protected void _unescapedVariable(String name) throws ProcessingException {
        indent();
        flushUnescaped();
        println();
        try {
            if (!expectsYield || !name.equals("yield")) {
                TemplateCompilerContext variable = context.getChild(name, ContextType.UNESCAPED_VAR);
                print("// unescaped variable: " + variable.currentEnclosedContextName());
                println();
                
                print(variable.unescapedRenderingCode());
                println();
            } else {
                if (foundYield)
                    throw new ProcessingException(position, "Yield can be used only once");
                if (context.isEnclosed())
                    throw new ProcessingException(position, "Unclosed \"" + context.currentEnclosedContextName() + "\" block before yield");
                else {
                    foundYield = true;
                    if (currentWriter().suppressesOutput())
                        currentWriter().enableOutput();
                    else
                        currentWriter().disableOutput();
                }
            }
        } catch (ContextException ex) {
            throw new ProcessingException(position, ex);
        }
    }
    
    private void indent() {
        if (atStartOfLine) {
            printCodeToWrite(indent);
        }
    }
    
    @Override
    protected void _specialCharacter(SpecialChar specialChar) throws ProcessingException {
        printCodeToWrite(specialChar.javaEscaped());
    }
    
    @Override
    public void _newline(NewlineChar c) throws ProcessingException {
        printCodeToWrite(c.javaEscaped());
    }
    
    @Override
    public void _text(String s) throws ProcessingException {
        indent();
        printCodeToWrite(s);
    }
    
    @Override
    public void _endOfFile() throws ProcessingException {
        flushUnescaped();
        if (!context.isEnclosed())
            return;
        else {
            throw new ProcessingException(position, "Unclosed \"" + context.currentEnclosedContextName() + "\" block at end of file");
        }
    }
    
    @Override
    public void close() throws IOException {
        reader.close();
    }

    static class RootTemplateCompiler extends TemplateCompiler {
        
        private final TemplateLoader templateLoader;
        private final CodeAppendable writer;
        private final Set<TemplateCompilerFlags.Flag> flags;
        
        public RootTemplateCompiler(
                String templateName,
                TemplateLoader templateLoader,
                CodeAppendable writer,
                TemplateCompilerContext context, 
                boolean expectsYield,
                Set<TemplateCompilerFlags.Flag> flags
                ) throws IOException {
            super(templateLoader.open(templateName), null, context, expectsYield);
            this.templateLoader = templateLoader;
            this.writer = writer;
            this.flags = flags;
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
        
        @Override
        public Set<TemplateCompilerFlags.Flag> flags() {
            return this.flags;
        }
        
    }
    
    static class SimpleTemplateCompiler extends RootTemplateCompiler {
        
        private SimpleTemplateCompiler(String templateName,
                TemplateLoader templateLoader,
                CodeAppendable writer,
                TemplateCompilerContext context,
                Set<TemplateCompilerFlags.Flag> flags
                ) throws IOException {
            super(templateName, templateLoader, writer, context, false, flags);
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
            super(templateName, templateLoader, writer, context, true, Set.of());

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
            super(templateName, templateLoader, writer, context, true, Set.of());
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
