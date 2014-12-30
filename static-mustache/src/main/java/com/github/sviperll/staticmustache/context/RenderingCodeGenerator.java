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
    private static String toTypeName(TypeMirror type) {
        if (type instanceof ArrayType) {
            ArrayType arrayType = (ArrayType)type;
            return toTypeName(arrayType.getComponentType()) + "[]";
        } else if (type instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType)type;
            TypeElement typeDeclaration = (TypeElement)declaredType.asElement();
            return typeDeclaration.getQualifiedName().toString();
        } else {
            throw new UnsupportedOperationException("Not supported");
        }
    }

    public static RenderingCodeGenerator createInstance(Types typeUtils, Elements elementUtils) {
        SpecialTypes types = new SpecialTypes(elementUtils, typeUtils);
        return new RenderingCodeGenerator(types, typeUtils);
    }

    private final SpecialTypes types;
    private final Types util;

    RenderingCodeGenerator(SpecialTypes types, Types util) {
        this.types = types;
        this.util = util;

    }
    String generateRenderingCode(TypeMirror type, String expression, VariableContext variables) throws TypeException {
        if (util.isAssignable(type, types._Renderable))
            return expression + ".createRenderer(" + variables.unescapedWriter() + ").render(); ";
        else if (util.isSameType(type, types._int))
            return variables.writer() + ".append(" + Integer.class.getName() + ".toString(" + expression + ")); ";
        else if (util.isSameType(type, types._short))
            return variables.writer() + ".append(" + Short.class.getName() + ".toString(" + expression + ")); ";
        else if (util.isSameType(type, types._long))
            return variables.writer() + ".append(" + Long.class.getName() + ".toString(" + expression + ")); ";
        else if (util.isSameType(type, types._byte))
            return variables.writer() + ".append(" + Byte.class.getName() + ".toString(" + expression + ")); ";
        else if (util.isSameType(type, types._char))
            return variables.writer() + ".append(" + Character.class.getName() + ".toString(" + expression + ")); ";
        else if (util.isSameType(type, types._float))
            return variables.writer() + ".append(" + Float.class.getName() + ".toString(" + expression + ")); ";
        else if (util.isSameType(type, types._double))
            return variables.writer() + ".append(" + Double.class.getName() + ".toString(" + expression + ")); ";
        else if (util.isAssignable(type, types._String))
            return variables.writer() + ".append(" + expression + "); ";
        else if (util.isAssignable(type, types._Integer))
            return variables.writer() + ".append(" + expression + ".toString()); ";
        else if (util.isAssignable(type, types._Long))
            return variables.writer() + ".append(" + expression + ".toString()); ";
        else if (util.isAssignable(type, types._Short))
            return variables.writer() + ".append(" + expression + ".toString()); ";
        else if (util.isAssignable(type, types._Byte))
            return variables.writer() + ".append(" + expression + ".toString()); ";
        else if (util.isAssignable(type, types._Character))
            return variables.writer() + ".append(" + expression + ".toString()); ";
        else if (util.isAssignable(type, types._Double))
            return variables.writer() + ".append(" + expression + ".toString()); ";
        else if (util.isAssignable(type, types._Float))
            return variables.writer() + ".append(" + expression + ".toString()); ";
        else
            throw new TypeException("Can't render " + expression + " expression of " + type + " type");
    }

    boolean isUnchecked(TypeMirror exceptionType) {
        return util.isAssignable(exceptionType, types._Error)
               || util.isAssignable(exceptionType, types._RuntimeException);
    }

    /**
     * creates TemplateCompilerContext instance
     *
     * @param element root of the data binding context
     * @param expression java expression of type correponding to given TypeElement
     * @param variables declared variables to use in generated code
     * @return new TemplateCompilerContext
     */
    public TemplateCompilerContext createTemplateCompilerContext(TypeElement element, String expression, VariableContext variables) {
        RootRenderingContext root = new RootRenderingContext(variables);
        DeclaredTypeRenderingContext rootRenderingContext = new DeclaredTypeRenderingContext(this, element, expression, root);
        return new TemplateCompilerContext(this, variables, rootRenderingContext);
    }

    RenderingContext createRenderingContext(TypeMirror type, String expression, RenderingContext enclosing) throws TypeException {
        if (util.isSameType(type, types._boolean)) {
            return new BooleanRenderingContext(expression, enclosing);
        } else if (util.isAssignable(type, types._Boolean)) {
            RenderingContext nullableContext = nullableRenderingContext(expression, enclosing);
            BooleanRenderingContext booleanContext = new BooleanRenderingContext(expression, nullableContext);
            return booleanContext;
        } else if (type instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType)type;
            Element contextElement = declaredType.asElement();
            if (!(contextElement instanceof TypeElement)) {
                throw new TypeException("Can't bind field: " + contextElement.getSimpleName() + " is " + contextElement.getKind());
            } else {
                RenderingContext nullableContext = nullableRenderingContext(expression, enclosing);
                DeclaredTypeRenderingContext declaredContext = new DeclaredTypeRenderingContext(this, (TypeElement)contextElement, expression, nullableContext);
                return declaredContext;
            }
        } else if (type instanceof ArrayType) {
            ArrayType arrayType = (ArrayType)type;
            TypeMirror componentType = arrayType.getComponentType();
            RenderingContext nullable = nullableRenderingContext(expression, enclosing);
            VariableContext variableContext = nullable.createEnclosedVariableContext();
            String indexVariableName = variableContext.introduceNewNameLike("i");
            RenderingContext variables = new VariablesRenderingContext(variableContext, nullable);
            ArrayRenderingContext array = new ArrayRenderingContext(componentType, expression, indexVariableName, this, variables);
            return createRenderingContext(componentType, array.componentExpession(), array);
        } else
            return new NoDataContext(expression, type, enclosing);
    }

    RenderingContext createInvertedRenderingContext(TypeMirror type, String expression, RenderingContext enclosing) throws TypeException {
        if (util.isSameType(type, types._boolean)) {
            return new BooleanRenderingContext("!(" + expression + ")", enclosing);
        } else if (util.isAssignable(type, types._Boolean)) {
            return new BooleanRenderingContext("(" + expression + ") == null || !(" + expression + ")", enclosing);
        } else if (type instanceof DeclaredType) {
            return new BooleanRenderingContext("(" + expression + ") == null", enclosing);
        } else if (type instanceof ArrayType) {
            return new BooleanRenderingContext("(" + expression + ") == null || (" + expression + ").length == 0", enclosing);
        } else
            throw new TypeException("Can't invert " + expression + " expression of " + type + " type");
    }

    private RenderingContext nullableRenderingContext(String expression, RenderingContext context) {
        return new BooleanRenderingContext(expression + " != null", context);
    }

    TypeMirror intType() {
        return types._int;
    }

    TypeMirror arrayType(TypeMirror elementType) {
        return util.getArrayType(elementType);
    }
}
