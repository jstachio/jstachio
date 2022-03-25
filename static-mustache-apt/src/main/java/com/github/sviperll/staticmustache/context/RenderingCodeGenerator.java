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
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

/**
 * This class allows to create TemplateCompilerContext instance
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class RenderingCodeGenerator {
    /**
     * Creates instance.
     *
     * @param javaModel language model to allow java expression manipulation
     * @param formatClass type declaration denoting text format. formatClass should not contain type variables.
     * @return
     */
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
    String generateRenderingCode(JavaExpression expression, VariableContext variables) throws TypeException {
        TypeMirror type = expression.type();
        String text = expression.text();
        if (type instanceof WildcardType)
            return generateRenderingCode(javaModel.expression(text, ((WildcardType)type).getExtendsBound()), variables);
        else if (javaModel.isSubtype(type, javaModel.getGenericDeclaredType(knownTypes._Renderable))) {
//            if (!javaModel.isSubtype(type, javaModel.getDeclaredType(knownTypes._Renderable, javaModel.getDeclaredType(templateFormatElement)))) {
//                throw new TypeException(MessageFormat.format("Can''t render {0} expression of {1} type: expression is Renderable, but wrong format", text, type));
//            } else {
                return text + ".render(" + variables.unescapedWriter()  + "); ";
//            }
        } else if (javaModel.isSameType(type, knownTypes._int))
            return variables.writer() + ".append(" + Integer.class.getName() + ".toString(" + text + ")); ";
        else if (javaModel.isSameType(type, knownTypes._short))
            return variables.writer() + ".append(" + Short.class.getName() + ".toString(" + text + ")); ";
        else if (javaModel.isSameType(type, knownTypes._long))
            return variables.writer() + ".append(" + Long.class.getName() + ".toString(" + text + ")); ";
        else if (javaModel.isSameType(type, knownTypes._byte))
            return variables.writer() + ".append(" + Byte.class.getName() + ".toString(" + text + ")); ";
        else if (javaModel.isSameType(type, knownTypes._char))
            return variables.writer() + ".append(" + Character.class.getName() + ".toString(" + text + ")); ";
        else if (javaModel.isSameType(type, knownTypes._float))
            return variables.writer() + ".append(" + Float.class.getName() + ".toString(" + text + ")); ";
        else if (javaModel.isSameType(type, knownTypes._double))
            return variables.writer() + ".append(" + Double.class.getName() + ".toString(" + text + ")); ";
        else if (javaModel.isSubtype(type, javaModel.getDeclaredType(knownTypes._String)))
            return variables.writer() + ".append(" + text + "); ";
        else if (javaModel.isSubtype(type, javaModel.getDeclaredType(knownTypes._Integer)))
            return variables.writer() + ".append(" + text + ".toString()); ";
        else if (javaModel.isSubtype(type, javaModel.getDeclaredType(knownTypes._Long)))
            return variables.writer() + ".append(" + text + ".toString()); ";
        else if (javaModel.isSubtype(type, javaModel.getDeclaredType(knownTypes._Short)))
            return variables.writer() + ".append(" + text + ".toString()); ";
        else if (javaModel.isSubtype(type, javaModel.getDeclaredType(knownTypes._Byte)))
            return variables.writer() + ".append(" + text + ".toString()); ";
        else if (javaModel.isSubtype(type, javaModel.getDeclaredType(knownTypes._Character)))
            return variables.writer() + ".append(" + text + ".toString()); ";
        else if (javaModel.isSubtype(type, javaModel.getDeclaredType(knownTypes._Double)))
            return variables.writer() + ".append(" + text + ".toString()); ";
        else if (javaModel.isSubtype(type, javaModel.getDeclaredType(knownTypes._Float)))
            return variables.writer() + ".append(" + text + ".toString()); ";
        else
            throw new TypeException(MessageFormat.format("Can''t render {0} expression of {1} type", text, type));
    }

    /**
     * creates TemplateCompilerContext instance.
     *
     * @param element root of the data binding context. Element should not contain type-variables.
     * @param expression java expression of type corresponding to given TypeElement
     * @param variables declared variables to use in generated code
     * @return new TemplateCompilerContext
     */
    public TemplateCompilerContext createTemplateCompilerContext(TypeElement element, String expression, VariableContext variables) {
        RootRenderingContext root = new RootRenderingContext(variables);
        JavaExpression javaExpression = javaModel.expression(expression, javaModel.getDeclaredType(element));
        DeclaredTypeRenderingContext rootRenderingContext = new DeclaredTypeRenderingContext(javaExpression, element, root);
        return new TemplateCompilerContext(this, variables, rootRenderingContext);
    }

    RenderingContext createRenderingContext(JavaExpression expression, RenderingContext enclosing) throws TypeException {
        if (expression.type() instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType)expression.type();
            return createRenderingContext(javaModel.expression(expression.text(), wildcardType.getExtendsBound()), enclosing);
        } else if (javaModel.isSubtype(expression.type(), javaModel.getGenericDeclaredType(knownTypes._Layoutable))) {
            if (!javaModel.isSubtype(expression.type(), javaModel.getDeclaredType(knownTypes._Layoutable, javaModel.getDeclaredType(templateFormatElement)))) {
                throw new TypeException(MessageFormat.format("Can''t render {0} expression of {1} type: expression is Layoutable, but wrong format", expression.text(), expression.type()));
            } else {
                VariableContext context = enclosing.createEnclosedVariableContext();
                return new LayoutableRenderingContext(expression, context, enclosing);
            }
        } else if (javaModel.isSameType(expression.type(), knownTypes._boolean)) {
            return new BooleanRenderingContext(expression.text(), enclosing);
        } else if (javaModel.isSubtype(expression.type(), javaModel.getDeclaredType(knownTypes._Boolean))) {
            RenderingContext nullableContext = nullableRenderingContext(expression, enclosing);
            BooleanRenderingContext booleanContext = new BooleanRenderingContext(expression.text(), nullableContext);
            return booleanContext;
        } else if (javaModel.isSubtype(expression.type(), javaModel.getGenericDeclaredType(knownTypes._Iterable))) {
            RenderingContext nullable = nullableRenderingContext(expression, enclosing);
            VariableContext variableContext = nullable.createEnclosedVariableContext();
            String elementVariableName = variableContext.introduceNewNameLike("element");
            RenderingContext variables = new VariablesRenderingContext(variableContext, nullable);
            IterableRenderingContext iterable = new IterableRenderingContext(expression, elementVariableName, variables);
            return createRenderingContext(iterable.elementExpession(), iterable);
        } else if (expression.type().getKind() == TypeKind.ARRAY) {
            RenderingContext nullable = nullableRenderingContext(expression, enclosing);
            VariableContext variableContext = nullable.createEnclosedVariableContext();
            String indexVariableName = variableContext.introduceNewNameLike("i");
            RenderingContext variables = new VariablesRenderingContext(variableContext, nullable);
            ArrayRenderingContext array = new ArrayRenderingContext(expression, indexVariableName, variables);
            return createRenderingContext(array.componentExpession(), array);
        } else if (expression.type().getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType)expression.type();
            RenderingContext nullableContext = nullableRenderingContext(expression, enclosing);
            DeclaredTypeRenderingContext declaredContext = new DeclaredTypeRenderingContext(expression, javaModel.asElement(declaredType), nullableContext);
            return declaredContext;
        } else
            return new NoDataContext(expression, enclosing);
    }

    RenderingContext createInvertedRenderingContext(JavaExpression expression, RenderingContext enclosing) throws TypeException {
        if (expression.type() instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType)expression.type();
            return createRenderingContext(javaModel.expression(expression.text(), wildcardType.getExtendsBound()), enclosing);
        } else if (javaModel.isSameType(expression.type(), knownTypes._boolean)) {
            return new BooleanRenderingContext("!(" + expression.text() + ")", enclosing);
        } else if (javaModel.isSubtype(expression.type(), javaModel.getDeclaredType(knownTypes._Boolean))) {
            return new BooleanRenderingContext("(" + expression.text() + ") == null || !(" + expression.text() + ")", enclosing);
        } else if (expression.type() instanceof DeclaredType) {
            return new BooleanRenderingContext("(" + expression.text() + ") == null", enclosing);
        } else if (expression.type() instanceof ArrayType) {
            return new BooleanRenderingContext("(" + expression.text() + ") == null || (" + expression.text() + ").length == 0", enclosing);
        } else
            throw new TypeException(MessageFormat.format("Can''t invert {0} expression of {1} type",
                                                         expression.text(),
                                                         expression.type()));
    }

    private RenderingContext nullableRenderingContext(JavaExpression expression, RenderingContext context) {
        return new BooleanRenderingContext(expression.text() + " != null", context);
    }
}
