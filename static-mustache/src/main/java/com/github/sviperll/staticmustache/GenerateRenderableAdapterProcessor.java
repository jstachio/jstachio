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
package com.github.sviperll.staticmustache;

import com.github.sviperll.staticmustache.token.ProcessingException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class GenerateRenderableAdapterProcessor extends AbstractProcessor {
    private static HashMap<String, Object> toScope(AnnotationMirror annotation) {
        HashMap<String, Object> scope = new HashMap<String, Object>();
        for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry: annotation.getElementValues().entrySet()) {
            scope.put(entry.getKey().getSimpleName().toString(), toScopeValue(entry.getValue()));
        }
        return scope;
    }

    private static Object toScopeValue(Object value) {
        if (value instanceof TypeMirror) {
            return toTypeName((TypeMirror)value);
        } else if (value instanceof VariableElement) {
            return toEnumConstantName((VariableElement)value);
        } else if (value instanceof AnnotationMirror) {
            return toScope((AnnotationMirror)value);
        } else if (value instanceof List) {
            List<Object> result = new ArrayList<Object>();
            @SuppressWarnings("unchecked")
            List<? extends AnnotationValue> annotationValues = (List<? extends AnnotationValue>)value;
            for (AnnotationValue annotationValue: annotationValues) {
                result.add(toScopeValue(annotationValue));
            }
            return result;
        } else if (value instanceof AnnotationValue) {
            AnnotationValue annotationValue = (AnnotationValue)value;
            return toScopeValue(annotationValue.getValue());
        } else {
            System.out.println(value.getClass().getName() + ": " + value);
            return value;
        }
    }

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

    private static String toEnumConstantName(VariableElement enumConstant) {
        return enumConstant.getSimpleName().toString();
    }

    private final List<String> errors = new ArrayList<String>();

    @Override
    public boolean process(Set<? extends TypeElement> processEnnotations,
                           RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            for (String error: errors) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, error);
            }
        } else {
            for (Element element: roundEnv.getElementsAnnotatedWith(GenerateRenderableAdapter.class)) {
                TypeElement classElement = (TypeElement)element;
                GenerateRenderableAdapter directive = element.getAnnotation(GenerateRenderableAdapter.class);
                writeRenderableAdapterClass(classElement, directive);
            }
        }
        return true;
    }

    private void writeRenderableAdapterClass(TypeElement element, GenerateRenderableAdapter directive) throws RuntimeException {
        String className = element.getQualifiedName().toString();
        String classSimpleName = element.getSimpleName().toString();
        PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
        String packageName = packageElement.getQualifiedName().toString();

        String adapterClassSimpleName;
        if (!directive.adapterName().equals(":auto"))
            adapterClassSimpleName = directive.adapterName();
        else
            adapterClassSimpleName = "Renderable" + element.getSimpleName().toString() + "Adapter";
        String adapterClassName = packageName + "." + adapterClassSimpleName;
        String adapterRendererClassSimpleName = adapterClassSimpleName + "Renderer";
        String adapterRendererClassName = adapterClassName + "." + adapterRendererClassSimpleName;
        String templatePath = directive.template();
        Charset templateCharset = directive.charset().equals(":default") ? Charset.defaultCharset() : Charset.forName(directive.charset());
        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(adapterClassName, element);
            OutputStream stream = sourceFile.openOutputStream();
            try {
                Writer outputWriter = new OutputStreamWriter(stream);
                try {
                    PrintWriter writer = new PrintWriter(outputWriter);
                    try {
                        writer.println("package " + packageName + ";");
                        writer.println("class " + adapterClassSimpleName + " implements " + Renderable.class.getName() + "{");
                        writer.println("    private final " + className + " data;");
                        writer.println("    public " + adapterClassSimpleName + "(" + className + " data) {");
                        writer.println("        this.data = data;");
                        writer.println("    }");
                        writer.println("    @Override");
                        writer.println("    public " + Renderer.class.getName() + " createRenderer(" + Appendable.class.getName() + " writer) {");
                        writer.println("        return new " + adapterRendererClassName + "(data, writer);");
                        writer.println("    }");
                        writer.println("    private static class " + adapterRendererClassSimpleName + " implements " + Renderer.class.getName() + " {");
                        writer.println("        private final " + Appendable.class.getName() + " writer;");
                        writer.println("        private final " + className + " data;");
                        writer.println("        public " + adapterRendererClassSimpleName + "(" + className + " data, " + Appendable.class.getName() + " writer) {");
                        writer.println("            this.writer = writer;");
                        writer.println("            this.data = data;");
                        writer.println("        }");
                        writer.println("        @Override");
                        writer.println("        public void render() throws " + IOException.class.getName() + " {");
                        TemplateCompilerManager compilerManager = new TemplateCompilerManager(processingEnv.getMessager(), writer);
                        FileObject resource = processingEnv.getFiler().getResource(StandardLocation.CLASS_PATH, "", templatePath);
                        Types typeUtils = processingEnv.getTypeUtils();
                        SpecialTypes types = new SpecialTypes(processingEnv.getElementUtils(), typeUtils);
                        TypeProcessor typeProcessor = new TypeProcessor(types, typeUtils);
                        compilerManager.compileTemplate(resource, templateCharset, new TypeElementContext(typeProcessor, element, "data", "writer"));
                        writer.println("        }");
                        writer.println("    }");
                        writer.println("}");
                    } finally {
                        writer.close();
                    }
                } finally {
                    outputWriter.close();
                }
            } finally {
                stream.close();
            }
        } catch (ProcessingException ex) {
            errors.add(ex.getMessage());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
