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
package com.github.sviperll.staticmustache.typeelementcontext;

import com.github.sviperll.staticmustache.TypeException;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class TemplateContext {
    private final EnclosedRelation enclosedRelation;
    private final String writerExpression;
    private final FieldContext field;
    private final TypeProcessor processor;

    public TemplateContext(TypeProcessor processor, String writerExpression, FieldContext field) {
        this(processor, writerExpression, field, null);
    }

    private TemplateContext(TypeProcessor processor, String writerExpression, FieldContext field, EnclosedRelation parent) {
        this.enclosedRelation = parent;
        this.writerExpression = writerExpression;
        this.field = field;
        this.processor = processor;
    }

    public String inline(String name) throws TypeException {
        ContextEntry entry = getEntry(name);
        return processor.inline(entry.type(), entry.expression(), writerExpression);
    }

    public String startOfBlock() {
        return field.startOfBlock();
    }

    public String endOfBlock() {
        return field.endOfBlock();
    }

    public TemplateContext createChild(String name) throws TypeException {
        ContextEntry entry = getEntry(name);
        FieldContext enclosedField = processor.createFieldContext(name, new NoStartEndFieldContext(field), entry.expression(), entry.type());
        return new TemplateContext(processor, writerExpression, enclosedField, new EnclosedRelation(name, this));
    }

    private ContextEntry getEntry(String name) throws TypeException {
        if (name.equals("."))
            return field.thisEntry();
        else
            return field.getEntry(name);
    }

    public boolean isEnclosed() {
        return enclosedRelation != null;
    }

    public String currentEnclosedContextName() {
        return enclosedRelation.name();
    }

    public TemplateContext parentContext() {
        return enclosedRelation.parentContext();
    }

    public String inlineThis() throws TypeException {
        ContextEntry entry = field.thisEntry();
        return processor.inline(entry.type(), entry.expression(), writerExpression);
    }
}
