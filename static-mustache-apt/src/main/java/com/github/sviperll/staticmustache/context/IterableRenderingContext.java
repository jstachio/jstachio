/*
 * Copyright (c) 2015, Victor Nazarov <asviraspossible@gmail.com>
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

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class IterableRenderingContext implements RenderingContext {
    private final JavaExpression expression;
    private final String elementVariableName;
    private final RenderingContext parent;

    public IterableRenderingContext(JavaExpression expression, String elementVariableName, RenderingContext parent) {
        this.expression = expression;
        this.elementVariableName = elementVariableName;
        this.parent = parent;
    }

    @Override
    public String beginSectionRenderingCode() {
        return parent.beginSectionRenderingCode()
               + String.format("for (%s %s: %s) { ",
                               elementExpession().type(),
                               elementVariableName,
                               expression.text());
    }

    @Override
    public String endSectionRenderingCode() {
        return " }" + parent.endSectionRenderingCode();
    }

    @Override
    public JavaExpression getDataOrDefault(String name, JavaExpression defaultValue) throws ContextException {
        return parent.getDataOrDefault(name, defaultValue);
    }

    @Override
    public JavaExpression currentExpression() {
        return expression;
    }

    @Override
    public VariableContext createEnclosedVariableContext() {
        return parent.createEnclosedVariableContext();
    }

    JavaExpression elementExpession() {
        DeclaredType iterableType = expression.model().getSupertype((DeclaredType)expression.type(), expression.model().knownTypes()._Iterable);
        TypeMirror elementType = iterableType.getTypeArguments().iterator().next();
        if (elementType instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType)elementType;
            elementType = wildcardType.getExtendsBound();
        }
        return expression.model().expression(elementVariableName, elementType);
    }
}
