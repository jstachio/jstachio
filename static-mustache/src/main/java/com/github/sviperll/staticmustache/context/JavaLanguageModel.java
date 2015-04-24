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

import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class JavaLanguageModel {
    public static JavaLanguageModel createInstance(Types types, Elements elements) {
        KnownTypes knownTypes = KnownTypes.createInstace(elements, types);
        return new JavaLanguageModel(types, knownTypes);
    }

    private final Types operations;
    private final KnownTypes knownTypes;
    JavaLanguageModel(Types operations, KnownTypes knownTypes) {
        this.operations = operations;
        this.knownTypes = knownTypes;
    }

    KnownTypes knownTypes() {
        return knownTypes;
    }

    DeclaredType getDeclaredType(TypeElement element, TypeMirror... typeArguments) {
        return operations.getDeclaredType(element, typeArguments);
    }

    boolean isSameType(TypeMirror first, TypeMirror second) {
        return operations.isSameType(first, second);
    }

    boolean isSubtype(TypeMirror subtype, TypeMirror supertype) {
        return operations.isSubtype(subtype, supertype);
    }

    boolean isUncheckedException(TypeMirror exceptionType) {
        return operations.isAssignable(exceptionType, operations.getDeclaredType(knownTypes._Error))
               || operations.isAssignable(exceptionType, operations.getDeclaredType(knownTypes._RuntimeException));
    }

    TypeMirror getArrayType(TypeMirror elementType) {
        return operations.getArrayType(elementType);
    }

    TypeMirror asMemberOf(DeclaredType containing, Element element) {
        return operations.asMemberOf(containing, element);
    }

    JavaExpression expression(String text, TypeMirror type) {
        return new JavaExpression(this, text, type);
    }

    TypeMirror getGenericDeclaredType(TypeElement element) {
        List<? extends TypeParameterElement> typeParameters = element.getTypeParameters();
        int numberOfParameters = typeParameters.size();
        List<TypeMirror> typeArguments = new ArrayList<TypeMirror>(numberOfParameters);
        for (int i = 0; i < numberOfParameters; i++) {
            typeArguments.add(operations.getWildcardType(null, null));
        }
        TypeMirror[] typeArgumentArray = new TypeMirror[typeArguments.size()];
        typeArgumentArray = typeArguments.toArray(typeArgumentArray);
        return getDeclaredType(element, typeArgumentArray);
    }

    DeclaredType getSupertype(DeclaredType type, TypeElement supertypeDeclaration) {
        if (type.asElement().equals(supertypeDeclaration))
            return type;
        else {
            List<? extends TypeMirror> supertypes = operations.directSupertypes(type);
            for (TypeMirror supertype: supertypes) {
                DeclaredType result = getSupertype((DeclaredType)supertype, supertypeDeclaration);
                if (result != null)
                    return result;
            }
            return null;
        }
    }

    TypeElement asElement(DeclaredType declaredType) {
        return (TypeElement)operations.asElement(declaredType);
    }
}
