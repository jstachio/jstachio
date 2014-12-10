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
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class TypeProcessor {
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

    private final SpecialTypes types;
    private final Types util;

    public TypeProcessor(SpecialTypes types, Types util) {
        this.types = types;
        this.util = util;

    }
    public String inline(TypeMirror type, String expression, String writer) throws TypeException {
        if (util.isAssignable(type, types._Renderable))
            return ifNotNull(expression, expression + ".createRenderer(" + writer + ").render(); ");
        else if (util.isAssignable(type, types._int))
            return writer + ".append(" + Integer.class.getName() + ".toString(" + expression + ")); ";
        else if (util.isAssignable(type, types._short))
            return writer + ".append(" + Short.class.getName() + ".toString(" + expression + ")); ";
        else if (util.isAssignable(type, types._long))
            return writer + ".append(" + Long.class.getName() + ".toString(" + expression + ")); ";
        else if (util.isAssignable(type, types._byte))
            return writer + ".append(" + Byte.class.getName() + ".toString(" + expression + ")); ";
        else if (util.isAssignable(type, types._char))
            return writer + ".append(" + Character.class.getName() + ".toString(" + expression + ")); ";
        else if (util.isAssignable(type, types._float))
            return writer + ".append(" + Float.class.getName() + ".toString(" + expression + ")); ";
        else if (util.isAssignable(type, types._double))
            return writer + ".append(" + Double.class.getName() + ".toString(" + expression + ")); ";
        else if (util.isAssignable(type, types._String))
            return ifNotNull(expression, writer + ".append(" + expression + "); ");
        else if (util.isAssignable(type, types._Integer))
            return ifNotNull(expression, writer + ".append(" + expression + ".toString()); ");
        else if (util.isAssignable(type, types._Long))
            return ifNotNull(expression, writer + ".append(" + expression + ".toString()); ");
        else if (util.isAssignable(type, types._Short))
            return ifNotNull(expression, writer + ".append(" + expression + ".toString()); ");
        else if (util.isAssignable(type, types._Byte))
            return ifNotNull(expression, writer + ".append(" + expression + ".toString()); ");
        else if (util.isAssignable(type, types._Character))
            return ifNotNull(expression, writer + ".append(" + expression + ".toString()); ");
        else if (util.isAssignable(type, types._Double))
            return ifNotNull(expression, writer + ".append(" + expression + ".toString()); ");
        else if (util.isAssignable(type, types._Float))
            return ifNotNull(expression, writer + ".append(" + expression + ".toString()); ");
        else
            throw new TypeException("Unsupported inlining: " + expression + ": unsupported type");
    }

    public boolean areUnchecked(TypeMirror exceptionType) {
        return util.isAssignable(exceptionType, types._Error)
               || util.isAssignable(exceptionType, types._RuntimeException);
    }

    private String ifNotNull(String expression, String statements) {
        return "if (" + expression + " != null) { " + statements + "} ";
    }

    FieldContext createFieldContext(String name, FieldContext parent, String expression, TypeMirror type) throws TypeException {
        if (type instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType)type;
            Element contextElement = declaredType.asElement();
            if (!(contextElement instanceof TypeElement)) {
                throw new TypeException("Can't use field " + name + " as context for block");
            } else {
                return new TypeElementFieldContext(this, (TypeElement)contextElement, expression, parent);
            }
        } else if (type instanceof ArrayType) {
            ArrayType arrayType = (ArrayType)type;
            TypeMirror componentType = arrayType.getComponentType();
            ArrayFieldContext arrayFieldContext = new ArrayFieldContext(expression, parent, componentType, this);
            return createFieldContext(name, arrayFieldContext, expression + "[i]", componentType);
        } else
            return new NothingFieldContext(expression, type, parent);
    }

    TypeMirror intType() {
        return types._int;
    }

    TypeMirror arrayType(TypeMirror elementType) {
        return util.getArrayType(elementType);
    }
}
