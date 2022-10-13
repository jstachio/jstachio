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
package io.jstach.apt.context;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import org.eclipse.jdt.annotation.Nullable;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
class DeclaredTypeRenderingContext implements RenderingContext {
    private final JavaExpression expression;
    private final TypeElement definitionElement;
    private final RenderingContext parent;

    DeclaredTypeRenderingContext(JavaExpression expression, TypeElement element, RenderingContext parent) {
        this.expression = expression;
        this.definitionElement = element;
        this.parent = parent;
    }

    @Override
    public String beginSectionRenderingCode() {
        return parent.beginSectionRenderingCode();
    }

    @Override
    public String endSectionRenderingCode() {
        return parent.endSectionRenderingCode();
    }
    
    @Override
    public @Nullable JavaExpression get(String name) throws ContextException {
        
        List<? extends Element> enclosedElements = definitionElement.getEnclosedElements();
        
        var all = JavaLanguageModel.getInstance().getElements().getAllMembers(definitionElement);
        
        var methods = ElementFilter.methodsIn(all).stream()
                .filter(e -> e.getModifiers().contains(Modifier.PUBLIC) 
                        && ! e.getModifiers().contains(Modifier.STATIC)
                        && e.getReturnType().getKind() != TypeKind.VOID
                        && e.getParameters().isEmpty()).toList();
        
        List<Element> allMethods = new ArrayList<>();
        /*
         * We add the enclosed methods which may include protected methods first.
         */
        allMethods.addAll(ElementFilter.methodsIn(enclosedElements));
        /*
         * Then we add all the other inherited methods that are public and not static
         */
        allMethods.addAll(methods);
        
        
        JavaExpression result = getMethodEntryOrDefault(allMethods, name, null);
        if (result != null)
            return result;
        result = getMethodEntryOrDefault(allMethods, getterName(name), null);
        if (result != null)
            return result;
        result = getFieldEntryOrDefault(enclosedElements, name, null);
        
        return result;
    }

    @Override
    public JavaExpression find(String name, Predicate<RenderingContext> filter) throws ContextException {
        JavaExpression result = null;
        if (filter.test(this)) {
            result = get(name);
        }
        if (result == null) {
            result = parent.find(name, filter);
        }
        return result;
    }

    private JavaExpression getMethodEntryOrDefault(List<? extends Element> elements, String methodName, JavaExpression defaultValue) throws ContextException {
        boolean nameFound = false;
        for (Element element: elements) {
            if (element.getKind() == ElementKind.METHOD && element.getSimpleName().contentEquals(methodName)) {
                nameFound = true;
                ExecutableType method;
                try {
                    method = expression.methodSignature(element);
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Unable to get " + element + " method signature for " + expression.type() + " type, defined at " + definitionElement, ex);
                }
                if (method.getParameterTypes().isEmpty()) {
                    if (element.getModifiers().contains(Modifier.PRIVATE)) {
                        throw new ContextException(MessageFormat.format("Refence to private method: ''{0}'': use package (default) access modifier to access method instead",
                                                                        methodName));
                    }
                    if (element.getModifiers().contains(Modifier.STATIC)) {
                        throw new ContextException(MessageFormat.format("Refence to static method: ''{0}'': only instance methods are accessible",
                                                                        methodName));
                    }
                    if (!areUnchecked(method.getThrownTypes())) {
                        throw new ContextException(MessageFormat.format("Refence to method throwing checked exceptions: ''{0}'': only unchecked exceptions are allowed",
                                                                        methodName));
                    }
                    return expression.methodCall(element);
                }
            }
        }
        if (!nameFound)
            return defaultValue;
        else {
            //We need to return null to let the lambda context to be found
            //TODO maybe declared type should check handle lambdas?
            return null;
//            throw new ContextException(MessageFormat.format("Refence to method with non-empty list of parameters: ''{0}'': only methods without parameters are supported",
//                                                            methodName));
        }
    }

    private JavaExpression getFieldEntryOrDefault(List<? extends Element> enclosedElements, String name, JavaExpression defaultValue) throws ContextException {
        for (Element element: enclosedElements) {
            if (element.getKind() == ElementKind.FIELD && element.getSimpleName().contentEquals(name)) {
                if (element.getModifiers().contains(Modifier.PRIVATE)) {
                    throw new ContextException(MessageFormat.format("Refence to private field: ''{0}'': use package (default) access modifier to access field instead",
                                                                    name));
                }
                if (element.getModifiers().contains(Modifier.STATIC)) {
                    throw new ContextException(MessageFormat.format("Refence to static field: ''{0}'': only instance fields are accessible",
                                                                    name));
                }
                return expression.fieldAccess(element);
            }
        }
        return defaultValue;
    }

    private String getterName(String name) {
        return "get" + name.substring(0, 1).toUpperCase(Locale.US) + name.substring(1);
    }

    private boolean areUnchecked(List<? extends TypeMirror> thrownTypes) {
        for (TypeMirror thrownType: thrownTypes) {
            if (!expression.model().isUncheckedException(thrownType))
                return false;
        }
        return true;
    }

    @Override
    public JavaExpression currentExpression() {
        return expression;
    }

    @Override
    public VariableContext createEnclosedVariableContext() {
        return parent.createEnclosedVariableContext();
    }
    
    @Override
    public @Nullable RenderingContext getParent() {
        return this.parent;
    }
    
    @Override
    public String description() {
        return toString();
    }

    @Override
    public String toString() {
        return "DeclaredTypeRenderingContext [\n\t\texpression=" + expression + ",\n\t\tdefinitionElement=" + definitionElement
                + ",\n\t\tparent=" + parent + "]";
    }
    
    
}
