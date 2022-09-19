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
package com.github.sviperll.staticmustache.context;

import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @see RenderingCodeGenerator#createTemplateCompilerContext
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class TemplateCompilerContext {
    private final EnclosedRelation enclosedRelation;
    private final RenderingContext context;
    private final RenderingCodeGenerator generator;
    private final VariableContext variables;

    TemplateCompilerContext(RenderingCodeGenerator processor, VariableContext variables, RenderingContext field) {
        this(processor, variables, field, null);
    }

    private TemplateCompilerContext(RenderingCodeGenerator processor, VariableContext variables, RenderingContext field, EnclosedRelation parent) {
        this.enclosedRelation = parent;
        this.context = field;
        this.generator = processor;
        this.variables = variables;
    }

    private String sectionBodyRenderingCode(VariableContext variables) throws ContextException {
        JavaExpression entry = context.currentExpression();
        try {
            return generator.generateRenderingCode(entry, variables);
        } catch (TypeException ex) {
            throw new ContextException("Unable to render field", ex);
        }
    }

    public String renderingCode() throws ContextException {
        return beginSectionRenderingCode() + sectionBodyRenderingCode(variables) + endSectionRenderingCode();
    }

    public String unescapedRenderingCode() throws ContextException {
        return beginSectionRenderingCode() + sectionBodyRenderingCode(variables.unescaped()) + endSectionRenderingCode();
    }

    public String beginSectionRenderingCode() {
        return context.beginSectionRenderingCode();
    }

    public String endSectionRenderingCode() {
        return context.endSectionRenderingCode();
    }

    public interface Printer {
        void print(TemplateCompilerContext c, State state);
        enum State {
            BEGIN,END
        }
    }
    
    public List<TemplateCompilerContext> getChildren(String name) throws ContextException {
        if (name.equals(".")) {
            return List.of(getChild(name));
        }
        List<String> names = splitNames(name);
        
        if (names.size() == 0) {
            throw new IllegalStateException("names");
        }
        List<TemplateCompilerContext> contexts = new ArrayList<>();
        TemplateCompilerContext tc = this;
        for (String n : names) {
            tc = tc.getChild(n);
            contexts.add(tc);
        }
        return contexts;
    }
    
    public TemplateCompilerContext getChild(String n) throws ContextException {
        if (n.equals(".")) {
            return new TemplateCompilerContext(generator, variables, new OwnedRenderingContext(context), new EnclosedRelation(n, this));
        }
        JavaExpression entry = context.getDataOrDefault(n, null);
        if (entry == null)
            throw new ContextException(MessageFormat.format("Field not found in current context: ''{0}''", n));
        RenderingContext enclosedField;
        try {
            enclosedField = generator.createRenderingContext(entry, new OwnedRenderingContext(context));
        } catch (TypeException ex) {
            throw new ContextException(MessageFormat.format("Can''t use ''{0}'' field for rendering", n), ex);
        }
        return new TemplateCompilerContext(generator, variables, enclosedField, new EnclosedRelation(n, this));
    }
    
    List<String> splitNames(String name) {
        return List.of(name.split("\\."));
    }

    public TemplateCompilerContext getInvertedChild(String name) throws ContextException {
        if (name.equals(".")) {
            throw new ContextException("Current section can't be inverted");
        } else {
            JavaExpression entry = context.getDataOrDefault(name, null);
            if (entry == null)
                throw new ContextException(MessageFormat.format("Field not found in current context: ''{0}''", name));
            RenderingContext enclosedField;
            try {
                enclosedField = generator.createInvertedRenderingContext(entry, new OwnedRenderingContext(context));
            } catch (TypeException ex) {
                throw new ContextException(MessageFormat.format("Can''t use ''{0}'' field for rendering", name), ex);
            }
            return new TemplateCompilerContext(generator, variables, enclosedField, new EnclosedRelation(name, this));
        }
    }

    public boolean isEnclosed() {
        return enclosedRelation != null;
    }

    public String currentEnclosedContextName() {
        return enclosedRelation.name();
    }

    public TemplateCompilerContext parentContext() {
        return enclosedRelation.parentContext();
    }

    public String unescapedWriterExpression() {
        return variables.unescapedWriter();
    }
}
