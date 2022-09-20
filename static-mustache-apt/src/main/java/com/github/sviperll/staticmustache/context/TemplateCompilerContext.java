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

import org.jspecify.nullness.Nullable;

/**
 * @see RenderingCodeGenerator#createTemplateCompilerContext
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class TemplateCompilerContext {
    private final @Nullable EnclosedRelation enclosedRelation;
    private final RenderingContext context;
    private final RenderingCodeGenerator generator;
    private final VariableContext variables;
    private final ArrayDeque<TemplateCompilerContext> pathStack;

    TemplateCompilerContext(RenderingCodeGenerator processor, VariableContext variables, RenderingContext field) {
        this(processor, variables, field, new ArrayDeque<>(), null);
    }
    
    private TemplateCompilerContext(RenderingCodeGenerator processor, VariableContext variables, RenderingContext field, 
            @Nullable EnclosedRelation parent) {
        this(processor, variables, field, new ArrayDeque<>(), parent);
    }

    private TemplateCompilerContext(RenderingCodeGenerator processor, VariableContext variables, RenderingContext field, 
            ArrayDeque<TemplateCompilerContext> pathStack,  @Nullable EnclosedRelation parent) {
        this.enclosedRelation = parent;
        this.context = field;
        this.generator = processor;
        this.variables = variables;
        this.pathStack = pathStack;
        this.pathStack.add(this);
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
        StringBuilder sb = new StringBuilder();
        var it = pathStack.iterator();
        while (it.hasNext()) {
            var i = it.next();
            sb.append(i.context.beginSectionRenderingCode());
        }
        return sb.toString();
    }

    public String endSectionRenderingCode() {
        StringBuilder sb = new StringBuilder();
        var it = pathStack.descendingIterator();
        while (it.hasNext()) {
            var i = it.next();
            sb.append(i.context.endSectionRenderingCode());
        }
        return sb.toString();
    }

    
    public TemplateCompilerContext getChild(String name, ChildType childType) throws ContextException {
        if (name.equals(".")) {
            return _getChild(name, childType);
        }
        List<String> names = splitNames(name);
        
        if (names.size() == 0) {
            throw new IllegalStateException("names");
        }
        ArrayList<TemplateCompilerContext> contexts = new ArrayList<>();
        TemplateCompilerContext tc = this;
        for (String n : names) {
            tc = tc._getChild(n, childType);
            contexts.add(tc);
        }
        tc.pathStack.clear();
        tc.pathStack.addAll(contexts);
        
        return tc;
    }

    List<String> splitNames(String name) {
        return List.of(name.split("\\."));
    }

    
    public enum ChildType {
        NORMAL,
        INVERTED;
    }
    
    private TemplateCompilerContext _getChild(String name, ChildType childType) throws ContextException {
        if (name.equals(".")) {
            return switch (childType) {
            case NORMAL -> new TemplateCompilerContext(generator, variables, new OwnedRenderingContext(context),
                    new ArrayDeque<>(),
                    new EnclosedRelation(name, this));
            case INVERTED -> throw new ContextException("Current section can't be inverted");
            };
        }
        if (name.contains(".")) {
            throw new IllegalStateException("dotted path not allowed here");
        }
        JavaExpression entry = context.getDataOrDefault(name, null);
        if (entry == null)
            throw new ContextException(MessageFormat.format("Field not found in current context: ''{0}''", name));
        RenderingContext enclosedField;
        try {
            enclosedField = switch (childType) {
            case NORMAL -> generator.createRenderingContext(entry, new OwnedRenderingContext(context));
            case INVERTED -> generator.createInvertedRenderingContext(entry, new OwnedRenderingContext(context));
            };
        } catch (TypeException ex) {
            throw new ContextException(MessageFormat.format("Can''t use ''{0}'' field for rendering", name), ex);
        }
        return new TemplateCompilerContext(generator, variables, enclosedField, new EnclosedRelation(name, this));
    }

    public boolean isEnclosed() {
        return enclosedRelation != null;
    }

    public String currentEnclosedContextName() {
        StringBuilder sb = new StringBuilder();
        for (var tc : pathStack) {
            if (! sb.isEmpty()) {
                sb.append(".");
            }
            sb.append(tc.enclosedRelation.name());
        }
        return sb.toString();
    }

    public TemplateCompilerContext parentContext() {
        return pathStack.getFirst().enclosedRelation.parentContext();
    }

    public String unescapedWriterExpression() {
        return variables.unescapedWriter();
    }
}
