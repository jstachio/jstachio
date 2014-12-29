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

import javax.lang.model.element.TypeElement;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class TemplateCompilerContext {
    public static TemplateCompilerContext createInstace(RenderingCodeGenerator codeGenerator, TypeElement element,
                                                        ContextVariables variables) {
        RenderingContext fieldContext = new DeclaredTypeRenderingContext(codeGenerator, element, variables.data());
        return new TemplateCompilerContext(codeGenerator, variables, fieldContext);
    }
    private final EnclosedRelation enclosedRelation;
    private final RenderingContext context;
    private final RenderingCodeGenerator generator;
    private final ContextVariables variables;

    TemplateCompilerContext(RenderingCodeGenerator processor, ContextVariables variables, RenderingContext field) {
        this(processor, variables, field, null);
    }

    private TemplateCompilerContext(RenderingCodeGenerator processor, ContextVariables variables, RenderingContext field, EnclosedRelation parent) {
        this.enclosedRelation = parent;
        this.context = field;
        this.generator = processor;
        this.variables = variables;
    }

    private String renderingCodeBody() throws ContextException {
        RenderingData entry = context.thisCurrentData();
        try {
            return generator.generateRenderingCode(entry.type(), entry.expression(), variables.writer());
        } catch (TypeException ex) {
            throw new ContextException("Unable to render field", ex);
        }
    }

    public String renderingCode() throws ContextException {
        return startOfRenderingCode() + renderingCodeBody() + endOfRenderingCode();
    }

    public String startOfRenderingCode() {
        return context.startOfRenderingCode();
    }

    public String endOfRenderingCode() {
        return context.endOfRenderingCode();
    }

    public TemplateCompilerContext createChild(String name) throws ContextException {
        if (name.equals(".")) {
            return new TemplateCompilerContext(generator, variables, new OwnedRenderingContext(context), new EnclosedRelation(name, this));
        } else {
            RenderingData entry = context.getDataOrDefault(name, null);
            if (entry == null)
                throw new ContextException("Field not found in current context: " + name);
            RenderingContext enclosedField;
            try {
                enclosedField = generator.createRenderingContext(entry.type(), entry.expression(), new OwnedRenderingContext(context));
            } catch (TypeException ex) {
                throw new ContextException("Can't use " + name + " for rendering", ex);
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
}
