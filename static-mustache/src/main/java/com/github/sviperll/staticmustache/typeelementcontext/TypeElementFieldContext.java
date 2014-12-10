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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class TypeElementFieldContext implements FieldContext {
    private final String expression;
    private final TypeElement thisElement;
    private final FieldContext parent;
    private final TypeProcessor utils;

    public TypeElementFieldContext(TypeProcessor utils, TypeElement element, String expression) {
        this(utils, element, expression, null);
    }

    TypeElementFieldContext(TypeProcessor utils, TypeElement element, String expression, FieldContext parent) {
        this.expression = expression;
        this.thisElement = element;
        this.parent = parent;
        this.utils = utils;
    }

    @Override
    public String startOfBlock() {
        return parent.startOfBlock() + "if (" + expression + " != null) {";
    }

    @Override
    public String endOfBlock() {
        return "}" + parent.endOfBlock();
    }

    @Override
    public ContextEntry getEntry(String name) throws TypeException {
        List<? extends Element> enclosedElements = thisElement.getEnclosedElements();
        for (Element element: enclosedElements) {
            if (element.getKind() == ElementKind.METHOD && element.getSimpleName().contentEquals(name)) {
                return getMethodEntry(enclosedElements, name, name);
            }
        }
        String getterName = getterName(name);
        for (Element element: enclosedElements) {
            if (element.getKind() == ElementKind.METHOD && element.getSimpleName().contentEquals(getterName)) {
                return getMethodEntry(enclosedElements, getterName, name);
            }
        }
        for (Element element: enclosedElements) {
            if (element.getKind() == ElementKind.FIELD && element.getSimpleName().contentEquals(name)) {
                VariableElement field = (VariableElement)element;
                return new ContextEntry(expression + "." + name, field.asType());
            }
        }
        if (parent == null)
            throw new TypeException("Unknown field: " + name);
        else
            return parent.getEntry(name);
    }

    private ContextEntry getMethodEntry(List<? extends Element> elements, String methodName, String entryName) throws TypeException {
        for (Element element: elements) {
            if (element.getKind() == ElementKind.METHOD && element.getSimpleName().contentEquals(methodName)) {
                ExecutableElement method = (ExecutableElement)element;
                if (method.getParameters().isEmpty()
                    && !method.getModifiers().contains(Modifier.STATIC)
                    && areUnchecked(method.getThrownTypes())) {
                    return new ContextEntry(expression + "." + methodName + "()", method.getReturnType());
                }
            }
        }
        throw new TypeException("Found method with " + methodName + " name but it is static and/or has arguments");
    }

    private String getterName(String name) {
        return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private boolean areUnchecked(List<? extends TypeMirror> thrownTypes) {
        for (TypeMirror thrownType: thrownTypes) {
            if (!utils.areUnchecked(thrownType))
                return false;
        }
        return true;
    }

    @Override
    public ContextEntry thisEntry() throws TypeException {
        return new ContextEntry(expression, thisElement.asType());
    }
}
