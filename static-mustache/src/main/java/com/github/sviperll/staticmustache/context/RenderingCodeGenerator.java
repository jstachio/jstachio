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

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * This class allows to create TemplateCompilerContext instance
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class RenderingCodeGenerator {
    public static RenderingCodeGenerator createInstance(JavaLanguageModel javaModel, TypeElement formatClass) {
        return new RenderingCodeGenerator(javaModel.knownTypes(), javaModel, formatClass);
    }

    private final KnownTypes knownTypes;
    private final JavaLanguageModel javaModel;
    private final TypeElement templateFormatElement;

    private RenderingCodeGenerator(KnownTypes types, JavaLanguageModel javaModel, TypeElement formatClass) {
        this.knownTypes = types;
        this.javaModel = javaModel;
        this.templateFormatElement = formatClass;

    }
    String generateRenderingCode(TypeMirror type, String expression, VariableContext variables) throws TypeException {
        if (javaModel.isAssignable(type, javaModel.getDeclaredType(knownTypes._Renderable, javaModel.getDeclaredType(templateFormatElement))))
            return expression + ".createRenderer(" + variables.unescapedWriter() + ").render(); ";
        else if (javaModel.isSameType(type, knownTypes._int))
            return variables.writer() + ".append(" + Integer.class.getName() + ".toString(" + expression + ")); ";
        else if (javaModel.isSameType(type, knownTypes._short))
            return variables.writer() + ".append(" + Short.class.getName() + ".toString(" + expression + ")); ";
        else if (javaModel.isSameType(type, knownTypes._long))
            return variables.writer() + ".append(" + Long.class.getName() + ".toString(" + expression + ")); ";
        else if (javaModel.isSameType(type, knownTypes._byte))
            return variables.writer() + ".append(" + Byte.class.getName() + ".toString(" + expression + ")); ";
        else if (javaModel.isSameType(type, knownTypes._char))
            return variables.writer() + ".append(" + Character.class.getName() + ".toString(" + expression + ")); ";
        else if (javaModel.isSameType(type, knownTypes._float))
            return variables.writer() + ".append(" + Float.class.getName() + ".toString(" + expression + ")); ";
        else if (javaModel.isSameType(type, knownTypes._double))
            return variables.writer() + ".append(" + Double.class.getName() + ".toString(" + expression + ")); ";
        else if (javaModel.isAssignable(type, javaModel.getDeclaredType(knownTypes._String)))
            return variables.writer() + ".append(" + expression + "); ";
        else if (javaModel.isAssignable(type, javaModel.getDeclaredType(knownTypes._Integer)))
            return variables.writer() + ".append(" + expression + ".toString()); ";
        else if (javaModel.isAssignable(type, javaModel.getDeclaredType(knownTypes._Long)))
            return variables.writer() + ".append(" + expression + ".toString()); ";
        else if (javaModel.isAssignable(type, javaModel.getDeclaredType(knownTypes._Short)))
            return variables.writer() + ".append(" + expression + ".toString()); ";
        else if (javaModel.isAssignable(type, javaModel.getDeclaredType(knownTypes._Byte)))
            return variables.writer() + ".append(" + expression + ".toString()); ";
        else if (javaModel.isAssignable(type, javaModel.getDeclaredType(knownTypes._Character)))
            return variables.writer() + ".append(" + expression + ".toString()); ";
        else if (javaModel.isAssignable(type, javaModel.getDeclaredType(knownTypes._Double)))
            return variables.writer() + ".append(" + expression + ".toString()); ";
        else if (javaModel.isAssignable(type, javaModel.getDeclaredType(knownTypes._Float)))
            return variables.writer() + ".append(" + expression + ".toString()); ";
        else
            throw new TypeException("Can't render " + expression + " expression of " + type + " type");
    }

    /**
     * creates TemplateCompilerContext instance
     *
     * @param element root of the data binding context
     * @param text java expression of type correponding to given TypeElement
     * @param variables declared variables to use in generated code
     * @return new TemplateCompilerContext
     */
    public TemplateCompilerContext createTemplateCompilerContext(TypeElement element, String text, VariableContext variables) {
        RootRenderingContext root = new RootRenderingContext(variables);
        JavaExpression expression = javaModel.expression(text, javaModel.getDeclaredType(element));
        DeclaredTypeRenderingContext rootRenderingContext = new DeclaredTypeRenderingContext(expression, element, root);
        return new TemplateCompilerContext(this, variables, rootRenderingContext);
    }

    RenderingContext createRenderingContext(JavaExpression expression, RenderingContext enclosing) throws TypeException {
        if (javaModel.isSameType(expression.type(), knownTypes._boolean)) {
            return new BooleanRenderingContext(expression.text(), enclosing);
        } else if (javaModel.isAssignable(expression.type(), javaModel.getDeclaredType(knownTypes._Boolean))) {
            RenderingContext nullableContext = nullableRenderingContext(expression, enclosing);
            BooleanRenderingContext booleanContext = new BooleanRenderingContext(expression.text(), nullableContext);
            return booleanContext;
        } else if (expression.type() instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType)expression.type();
            RenderingContext nullableContext = nullableRenderingContext(expression, enclosing);
            DeclaredTypeRenderingContext declaredContext = new DeclaredTypeRenderingContext(expression, (TypeElement)declaredType.asElement(), nullableContext);
            return declaredContext;
        } else if (expression.type() instanceof ArrayType) {
            ArrayType arrayType = (ArrayType)expression.type();
            TypeMirror componentType = arrayType.getComponentType();
            RenderingContext nullable = nullableRenderingContext(expression, enclosing);
            VariableContext variableContext = nullable.createEnclosedVariableContext();
            String indexVariableName = variableContext.introduceNewNameLike("i");
            RenderingContext variables = new VariablesRenderingContext(variableContext, nullable);
            ArrayRenderingContext array = new ArrayRenderingContext(expression, indexVariableName, variables);
            return createRenderingContext(array.componentExpession(), array);
        } else
            return new NoDataContext(expression, enclosing);
    }

    RenderingContext createInvertedRenderingContext(JavaExpression expression, RenderingContext enclosing) throws TypeException {
        if (javaModel.isSameType(expression.type(), knownTypes._boolean)) {
            return new BooleanRenderingContext("!(" + expression.text() + ")", enclosing);
        } else if (javaModel.isAssignable(expression.type(), javaModel.getDeclaredType(knownTypes._Boolean))) {
            return new BooleanRenderingContext("(" + expression.text() + ") == null || !(" + expression.text() + ")", enclosing);
        } else if (expression.type() instanceof DeclaredType) {
            return new BooleanRenderingContext("(" + expression.text() + ") == null", enclosing);
        } else if (expression.type() instanceof ArrayType) {
            return new BooleanRenderingContext("(" + expression.text() + ") == null || (" + expression.text() + ").length == 0", enclosing);
        } else
            throw new TypeException("Can't invert " + expression.text() + " expression of " + expression.type() + " type");
    }

    private RenderingContext nullableRenderingContext(JavaExpression expression, RenderingContext context) {
        return new BooleanRenderingContext(expression.text() + " != null", context);
    }
}
