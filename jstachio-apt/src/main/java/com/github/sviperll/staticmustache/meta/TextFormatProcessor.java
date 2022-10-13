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
package com.github.sviperll.staticmustache.meta;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import io.jstach.text.formats.TextFormat;

/**
 *
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 */
@SupportedAnnotationTypes("com.github.sviperll.meta.TextFormat")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class TextFormatProcessor extends AbstractProcessor {
    private final List<ElementMessage> errors = new ArrayList<ElementMessage>();

    @Override
    public boolean process(Set<? extends TypeElement> processEnnotations,
                           RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            for (ElementMessage error: errors) {
                Element element = processingEnv.getElementUtils().getTypeElement(error.qualifiedElementName());
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, error.message(), element);
            }
        } else {
            for (Element element: roundEnv.getElementsAnnotatedWith(TextFormat.class)) {
                TextFormat directive = element.getAnnotation(TextFormat.class);
                processElement((TypeElement)element, directive);
            }
        }
        return true;
    }

    private void processElement(TypeElement templateFormatElement, TextFormat directive) {
        if (!templateFormatElement.getTypeParameters().isEmpty()) {
            Object[] arguments = new Object[] {templateFormatElement.getQualifiedName(), TextFormat.class.getName()};
            String message = MessageFormat.format("{0} class annotated with {1} annotation should not contain type variables", arguments);
            errors.add(ElementMessage.of(templateFormatElement, message));
        }
        ExecutableElement method = getCreateEscapingAppendableMethod(templateFormatElement, directive);
        if (method == null) {
            Object[] arguments = new Object[] {templateFormatElement.getQualifiedName(), TextFormat.class.getName(), directive.createEscapingAppendableMethodName()};
            String message = MessageFormat.format("{0} class annotated with {1} annotation should contain {2} method:\n    public static Appendable {2}(Appendable appendable)", arguments);
            errors.add(ElementMessage.of(templateFormatElement, message));
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Found " + templateFormatElement.getQualifiedName() + " text format", templateFormatElement);
    }
    private ExecutableElement getCreateEscapingAppendableMethod(TypeElement templateFormatElement, TextFormat directive) {
        List<? extends Element> elements = templateFormatElement.getEnclosedElements();
        for (Element element: elements) {
            if (element.getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement)element;
                Set<Modifier> modifiers = method.getModifiers();
                if (method.getSimpleName().contentEquals(directive.createEscapingAppendableMethodName())
                    && modifiers.contains(Modifier.PUBLIC)
                    && modifiers.contains(Modifier.STATIC)
                    && isAppendableToAppendable(method)
                    && !hasCheckedExceptions(method)) {
                    return method;
                }
            }
        }
        return null;
    }
    private boolean isAppendableToAppendable(ExecutableElement method) {
        List<? extends VariableElement> parameters = method.getParameters();
        if (parameters.size() != 1) {
            return false;
        } else {
            TypeMirror returnType = method.getReturnType();
            TypeMirror argumentType = parameters.iterator().next().asType();
            TypeElement appendableElement = processingEnv.getElementUtils().getTypeElement(Appendable.class.getName());
            TypeMirror appendableType = appendableElement.asType();
            return processingEnv.getTypeUtils().isSubtype(returnType, appendableType)
                && processingEnv.getTypeUtils().isSubtype(appendableType, argumentType);
        }
    }

    private boolean hasCheckedExceptions(ExecutableElement method) {
        List<? extends TypeMirror> thrownTypes = method.getThrownTypes();
        for (TypeMirror thrownType: thrownTypes) {
            if (isCheckedException(thrownType)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCheckedException(TypeMirror thrownType) {
        TypeElement runtimeExceptionElement = processingEnv.getElementUtils().getTypeElement(RuntimeException.class.getName());
        TypeMirror runtimeExceptionType = runtimeExceptionElement.asType();
        TypeElement errorElement = processingEnv.getElementUtils().getTypeElement(Error.class.getName());
        TypeMirror errorType = errorElement.asType();
        return !processingEnv.getTypeUtils().isSubtype(thrownType, errorType)
               && !processingEnv.getTypeUtils().isSubtype(thrownType, runtimeExceptionType);
    }
}
