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

import com.github.sviperll.staticmustache.context.ContextVariables;
import com.github.sviperll.staticmustache.context.TemplateCompilerContext;
import com.github.sviperll.staticmustache.context.RenderingCodeGenerator;
import com.github.sviperll.texttemplates.TemplateFormat;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class GenerateRenderableAdapterProcessor extends AbstractProcessor {
    private static String formatErrorMessage(Position position, String message) {
        String formatString = "%s:%d: error: %s%n%s%n%s%nsymbol: mustache directive%nlocation: mustache template";
        Object[] fields = new Object[] {
            position.fileName(),
            position.row(),
            message,
            position.currentLine(),
            columnPositioningString(position.col()),
        };
        return String.format(formatString, fields);
    }

    private static String columnPositioningString(int col) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < col - 1; i++)
            builder.append(' ');
        builder.append('^');
        return builder.toString();
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
            Element generateRenderableAdapterElement = processingEnv.getElementUtils().getTypeElement(GenerateRenderableAdapter.class.getName());
            for (Element element: roundEnv.getElementsAnnotatedWith(GenerateRenderableAdapter.class)) {
                TypeElement classElement = (TypeElement)element;
                List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
                AnnotationMirror directive = null;
                for (AnnotationMirror annotationMirror: annotationMirrors) {
                    if (processingEnv.getTypeUtils().isSubtype(annotationMirror.getAnnotationType(), generateRenderableAdapterElement.asType()))
                        directive = annotationMirror;
                }
                writeRenderableAdapterClass(classElement, directive);
            }
            Element generateRenderableAdaptersElement = processingEnv.getElementUtils().getTypeElement(GenerateRenderableAdapters.class.getName());
            for (Element element: roundEnv.getElementsAnnotatedWith(GenerateRenderableAdapters.class)) {
                TypeElement classElement = (TypeElement)element;
                List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
                for (AnnotationMirror mirror: annotationMirrors) {
                    if (processingEnv.getTypeUtils().isSubtype(mirror.getAnnotationType(), generateRenderableAdaptersElement.asType())) {
                        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = mirror.getElementValues();
                        for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry: elementValues.entrySet()) {
                            if (entry.getKey().getSimpleName().contentEquals("value")) {
                                @SuppressWarnings("unchecked")
                                List<? extends AnnotationValue> directives = (List<? extends AnnotationValue>)entry.getValue().getValue();
                                for (AnnotationValue directiveValue: directives) {
                                    AnnotationMirror directive = (AnnotationMirror)directiveValue.getValue();
                                    writeRenderableAdapterClass(classElement, directive);
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private void writeRenderableAdapterClass(TypeElement element, AnnotationMirror directiveMirror) throws RuntimeException {
        Method templateFormatMethod;
        Method adapterNameMethod;
        Method templateMethod;
        Method charsetMethod;
        try {
            templateFormatMethod = GenerateRenderableAdapter.class.getDeclaredMethod("templateFormat");
            adapterNameMethod = GenerateRenderableAdapter.class.getDeclaredMethod("adapterName");
            templateMethod = GenerateRenderableAdapter.class.getDeclaredMethod("template");
            charsetMethod = GenerateRenderableAdapter.class.getDeclaredMethod("charset");
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        } catch (SecurityException ex) {
            throw new RuntimeException(ex);
        }

        String className = element.getQualifiedName().toString();
        PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
        String packageName = packageElement.getQualifiedName().toString();

        String templatePath = null;
        String directiveAdapterName = null;
        String directiveCharset = null;
        TypeElement templateFormatElement = null;
        Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues = processingEnv.getElementUtils().getElementValuesWithDefaults(directiveMirror);
        for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry: annotationValues.entrySet()) {
            if (entry.getKey().getSimpleName().contentEquals(templateFormatMethod.getName())) {
                AnnotationValue value = entry.getValue();
                Object templateFormatValue = value.getValue();
                DeclaredType templateFormatType = (DeclaredType)templateFormatValue;
                templateFormatElement = (TypeElement)templateFormatType.asElement();
            } else if (entry.getKey().getSimpleName().contentEquals(adapterNameMethod.getName())) {
                directiveAdapterName = (String)entry.getValue().getValue();
            } else if (entry.getKey().getSimpleName().contentEquals(templateMethod.getName())) {
                templatePath = (String)entry.getValue().getValue();
            } else if (entry.getKey().getSimpleName().contentEquals(charsetMethod.getName())) {
                directiveCharset = (String)entry.getValue().getValue();
            }
        }
        if (templateFormatElement == null)
            throw new RuntimeException(templateFormatMethod.getName() + " should always be defined in " + GenerateRenderableAdapter.class.getName() + " annotation");
        if (directiveAdapterName == null)
            throw new RuntimeException(adapterNameMethod.getName() + " should always be defined in " + GenerateRenderableAdapter.class.getName() + " annotation");
        if (directiveCharset == null)
            throw new RuntimeException(charsetMethod.getName() + " should always be defined in " + GenerateRenderableAdapter.class.getName() + " annotation");
        if (templatePath == null)
            throw new RuntimeException(templateMethod.getName() + " should always be defined in " + GenerateRenderableAdapter.class.getName() + " annotation");
        String adapterClassSimpleName;
        if (!directiveAdapterName.equals(":auto"))
            adapterClassSimpleName = directiveAdapterName;
        else
            adapterClassSimpleName = "Renderable" + element.getSimpleName().toString() + "Adapter";
        String adapterClassName = packageName + "." + adapterClassSimpleName;
        String adapterRendererClassSimpleName = adapterClassSimpleName + "Renderer";
        String adapterRendererClassName = adapterClassName + "." + adapterRendererClassSimpleName;
        Charset templateCharset = directiveCharset.equals(":default") ? Charset.defaultCharset() : Charset.forName(directiveCharset);
        try {
            TemplateFormat templateFormatAnnotation = templateFormatElement.getAnnotation(TemplateFormat.class);
            if (templateFormatAnnotation == null) {
                throw new DeclarationException(templateFormatElement.getQualifiedName() + " class is used as a template format, but not marked with " + TemplateFormat.class.getName() + " annotation");
            }
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            try {
                writer.println("package " + packageName + ";");
                writer.println("class " + adapterClassSimpleName + " implements " + Renderable.class.getName() + "<" + templateFormatElement.getQualifiedName() + "> {");
                writer.println("    private final " + className + " data;");
                writer.println("    public " + adapterClassSimpleName + "(" + className + " data) {");
                writer.println("        this.data = data;");
                writer.println("    }");
                writer.println("    @Override");
                writer.println("    public " + Renderer.class.getName() + " createRenderer(" + Appendable.class.getName() + " unescapedWriter) {");
                writer.println("        " + Appendable.class.getName() + " writer = " + templateFormatElement.getQualifiedName() + "." + templateFormatAnnotation.createEscapingAppendableMethodName() + "(unescapedWriter);");
                writer.println("        return new " + adapterRendererClassName + "(data, writer, unescapedWriter);");
                writer.println("    }");
                writer.println("    private static class " + adapterRendererClassSimpleName + " implements " + Renderer.class.getName() + " {");
                writer.println("        private final " + Appendable.class.getName() + " unescapedWriter;");
                writer.println("        private final " + Appendable.class.getName() + " writer;");
                writer.println("        private final " + className + " data;");
                writer.println("        public " + adapterRendererClassSimpleName + "(" + className + " data, " + Appendable.class.getName() + " writer, " + Appendable.class.getName() + " unescapedWriter) {");
                writer.println("            this.writer = writer;");
                writer.println("            this.unescapedWriter = unescapedWriter;");
                writer.println("            this.data = data;");
                writer.println("        }");
                writer.println("        @Override");
                writer.println("        public void render() throws " + IOException.class.getName() + " {");
                TemplateCompilerManager compilerManager = new TemplateCompilerManager(processingEnv.getMessager(), writer);
                FileObject resource = processingEnv.getFiler().getResource(StandardLocation.CLASS_PATH, "", templatePath);
                RenderingCodeGenerator codeGenerator = RenderingCodeGenerator.createInstance(processingEnv.getTypeUtils(), processingEnv.getElementUtils());
                ContextVariables variables = new ContextVariables("data", "writer", "unescapedWriter");
                TemplateCompilerContext context = TemplateCompilerContext.createInstace(codeGenerator, element, variables);
                compilerManager.compileTemplate(resource, templateCharset, context);
                writer.println("        }");
                writer.println("    }");
                writer.println("}");
            } finally {
                writer.close();
            }
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(adapterClassName, element);
            OutputStream stream = sourceFile.openOutputStream();
            try {
                Writer outputWriter = new OutputStreamWriter(stream);
                try {
                    outputWriter.append(stringWriter.getBuffer().toString());
                } finally {
                    outputWriter.close();
                }
            } finally {
                stream.close();
            }
        } catch (ProcessingException ex) {
            String errorMessage = formatErrorMessage(ex.position(), ex.getMessage());
            errors.add(errorMessage);
        } catch (DeclarationException ex) {
            errors.add(ex.getMessage());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
