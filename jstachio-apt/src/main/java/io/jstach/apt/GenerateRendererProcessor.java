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
package io.jstach.apt;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.kohsuke.MetaInfServices;

import io.jstach.Appender;
import io.jstach.RenderFunction;
import io.jstach.Renderable;
import io.jstach.Renderer;
import io.jstach.annotation.AutoFormat;
import io.jstach.annotation.GenerateRenderer;
import io.jstach.annotation.GenerateRenderers;
import io.jstach.annotation.Template;
import io.jstach.annotation.TemplateCompilerFlags;
import io.jstach.annotation.TextFormat;
import io.jstach.apt.TemplateCompilerLike.TemplateCompilerType;
import io.jstach.apt.context.JavaLanguageModel;
import io.jstach.apt.context.RenderingCodeGenerator;
import io.jstach.apt.context.TemplateCompilerContext;
import io.jstach.apt.context.VariableContext;
import io.jstach.apt.meta.ElementMessage;
import io.jstach.apt.prism.GenerateRendererPrism;
import io.jstach.apt.prism.TemplateBasePathPrism;
import io.jstach.apt.prism.TemplateCompilerFlagsPrism;
import io.jstach.apt.prism.TemplateFormatterTypesPrism;
import io.jstach.apt.prism.TemplateInterfacePrism;
import io.jstach.apt.prism.TemplateMappingPrism;
import io.jstach.apt.prism.TemplatePrism;
import io.jstach.spi.Formatter;
import io.jstach.spi.RenderService;
import io.jstach.text.formats.Html;

@MetaInfServices(value=Processor.class)
@SupportedAnnotationTypes("*")
public class GenerateRendererProcessor extends AbstractProcessor {
	
	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}
	
    private static String formatErrorMessage(Position position, @Nullable String message) {
        message = message == null ? "" : message;
        String formatString = "%s:%d: error: %s%n%s%n%s%nsymbol: mustache directive%nlocation: mustache template";
        @Nullable Object @NonNull [] fields = new @Nullable Object @NonNull [] {
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

    private final List<ElementMessage> errors = new ArrayList<ElementMessage>();

    @Override
    public boolean process(Set<? extends TypeElement> processEnnotations,
                           RoundEnvironment roundEnv) {
        try {
            return _process(processEnnotations, roundEnv);
        } catch (AnnotatedException e) {
            e.report(processingEnv.getMessager());
            return true;
        }
    }
    
    private boolean _process(Set<? extends TypeElement> processEnnotations,
                           RoundEnvironment roundEnv)  throws AnnotatedException {
        if (roundEnv.processingOver()) {
            for (ElementMessage error: errors) {
                TypeElement element = processingEnv.getElementUtils().getTypeElement(error.qualifiedElementName());
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, error.message(), element);
            }
        } else {
            /*
             * Lets just bind the damn utils so that we do not have to pass them around everywhere
             */
            JavaLanguageModel.createInstance(processingEnv.getTypeUtils(), processingEnv.getElementUtils(), processingEnv.getMessager());
            Element generateRenderableAdapterElement = processingEnv.getElementUtils().getTypeElement(GenerateRenderer.class.getName());
            for (Element element: roundEnv.getElementsAnnotatedWith(GenerateRenderer.class)) {
                TypeElement classElement = (TypeElement)element;
                List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
                AnnotationMirror directive = null;
                for (AnnotationMirror annotationMirror: annotationMirrors) {
                    if (processingEnv.getTypeUtils().isSubtype(annotationMirror.getAnnotationType(), generateRenderableAdapterElement.asType()))
                        directive = annotationMirror;
                }
                assert directive != null;
                writeRenderableAdapterClass(classElement, directive);
            }
            Element generateRenderableAdaptersElement = processingEnv.getElementUtils().getTypeElement(GenerateRenderers.class.getName());
            for (Element element: roundEnv.getElementsAnnotatedWith(GenerateRenderers.class)) {
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
                                    assert directive != null;
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
    
    
    private String resolveBasePath(TypeElement element) {
        PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
        TemplateBasePathPrism prism = TemplateBasePathPrism.getInstanceOn(packageElement);
        if (prism == null) {
            return "";
        }
        String basePath = prism.value();
        if (basePath.equals("")) {
            basePath = packageElement.getQualifiedName().toString().replace(".", "/") + "/";
        }
        return basePath;
    }
    
    private List<String> resolveBaseInterface(TypeElement element) {
        PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
        TemplateInterfacePrism prism = TemplateInterfacePrism.getInstanceOn(packageElement);
        if (prism != null) {
            var tm =  prism.value();
            assert tm != null;
            return List.of(getTypeName(tm));
        }
        return List.of();
    }
    
    private Map<String, NamedTemplate> resolveTemplatePaths(TypeElement element) {

        Map<String, NamedTemplate> paths = new LinkedHashMap<>();
        var prism = TemplateMappingPrism.getInstanceOn(element);
        if (prism != null) {
            var tps = prism.value();
            for (TemplatePrism tp : tps) {
                NamedTemplate nt;
                String path = tp.path();
                String name = tp.name();
                assert name != null;
                String template = tp.template();
                
                if (! path.isBlank() ) {
                    nt = new NamedTemplate.FileTemplate(name, path);
                }
                else if (! template.equals(Template.NOT_SET)) {
                    nt = new NamedTemplate.InlineTemplate(name, template);
                }
                else {
                    nt = new NamedTemplate.FileTemplate(name, name);

                }
                paths.put(name, nt);
            }
        }
        return paths;
    }
    
    private Set<TemplateCompilerFlags.Flag> resolveFlags(TypeElement element) {
        var prism = TemplateCompilerFlagsPrism.getInstanceOn(element);
        var flags = EnumSet.noneOf(TemplateCompilerFlags.Flag.class);
        if (prism != null) {
            prism.flags().stream().map(TemplateCompilerFlags.Flag::valueOf).forEach(flags::add);
        }
        return Collections.unmodifiableSet(flags);
    }

    
    
    private String getTypeName(TypeMirror tm) {
       var e = ((DeclaredType) tm).asElement();
       var te = (TypeElement) e;
       return te.getQualifiedName().toString();
    }
    
    private FormatterTypes getFormatterTypes(TypeElement element) {
        PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
        TemplateFormatterTypesPrism prism = TemplateFormatterTypesPrism.getInstanceOn(packageElement);
        if (prism == null) {
            return FormatterTypes.acceptOnlyKnownTypes();
        }
        List<String> classNames = prism.types().stream().map(tm -> getTypeName(tm)).toList();
        List<String> patterns = prism.patterns().stream().toList();
        return new FormatterTypes.ConfiguredFormatterTypes(classNames, patterns);
    }
    
    
    private void writeRenderableAdapterClass(TypeElement element, AnnotationMirror directiveMirror) throws AnnotatedException {

        String templatePath = null;
        String directiveAdapterName = null;
        String directiveCharset = null;
        TypeElement templateFormatElement = null;
        
        GenerateRendererPrism gp = GenerateRendererPrism.getInstance(directiveMirror);
        
        if (gp == null) {
            throw new AnnotatedException(element, "Missing annotation. bug.");
        }
        templatePath = gp.template();
        directiveAdapterName = gp.adapterName();
        directiveCharset = gp.charset();
        TypeMirror templateFormatType = gp.templateFormat();
        if (templateFormatType instanceof DeclaredType dt) {
            templateFormatElement = (TypeElement) dt.asElement();
        }
        else {
            throw new ClassCastException("Expecting DeclaredType for templateFormat " + gp.templateFormat());
        }
        

        String adapterClassSimpleName;
        if (!directiveAdapterName.equals(":auto"))
            adapterClassSimpleName = directiveAdapterName;
        else {
            adapterClassSimpleName = element.getSimpleName().toString() + "Renderer";

        }
        Charset templateCharset = directiveCharset.equals(":default") ? Charset.defaultCharset() : Charset.forName(directiveCharset);
        try {
            @Nullable TextFormat templateFormatAnnotation = templateFormatElement.getAnnotation(TextFormat.class);
            if (templateFormatAnnotation == null) {
                throw new DeclarationException(templateFormatElement.getQualifiedName() + " class is used as a template format, but not marked with " + TextFormat.class.getName() + " annotation");
            }
            
            /*
             * TODO clean this up to resolve format
             */
            var autoFormatElement = JavaLanguageModel.getInstance().getElements().getTypeElement(AutoFormat.class.getName());
            if( JavaLanguageModel.getInstance().isSameType(autoFormatElement.asType(), templateFormatElement.asType())) {
                templateFormatElement = JavaLanguageModel.getInstance().getElements().getTypeElement(Html.class.getName());
                if (templateFormatElement == null) {
                    throw new DeclarationException("Missing default TextFormat class of Html");
                }
            }
            
            if (!element.getTypeParameters().isEmpty()) {
                throw new DeclarationException("Can't generate renderable adapter for class with type variables: " + element.getQualifiedName());
            }
            StringWriter stringWriter = new StringWriter();
            
            String basePath = resolveBasePath(element);
            if (! templatePath.startsWith("/")) {
                templatePath = basePath + templatePath;
            }
            List<String> ifaces = resolveBaseInterface(element);
            FormatterTypes formatterTypes = getFormatterTypes(element);
            Map<String,NamedTemplate> templatePaths = resolveTemplatePaths(element);
            Set<TemplateCompilerFlags.Flag> flags = resolveFlags(element);
            
            try (SwitchablePrintWriter switchablePrintWriter = SwitchablePrintWriter.createInstance(stringWriter)){
                //FileObject templateBinaryResource = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", templatePath);
                
                //TODO pass basepath
                TextFileObject templateResource = new TextFileObject(Objects.requireNonNull(processingEnv), templateCharset);
                JavaLanguageModel javaModel = JavaLanguageModel.getInstance();
                RenderingCodeGenerator codeGenerator = RenderingCodeGenerator.createInstance(javaModel, formatterTypes, templateFormatElement);
                CodeWriter codeWriter = new CodeWriter(switchablePrintWriter, codeGenerator, templatePaths, flags);
                ClassWriter writer = new ClassWriter(codeWriter, element, templateResource, templatePath);

                writer.writeRenderableAdapterClass(adapterClassSimpleName, templateFormatElement, ifaces);
            }
            PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
            String packageName = packageElement.getQualifiedName().toString();
            String adapterClassName = packageName + "." + adapterClassSimpleName;
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(adapterClassName, element);
            OutputStream stream = sourceFile.openOutputStream();
            try {
                Writer outputWriter = new OutputStreamWriter(stream, Charset.defaultCharset());
                try {
                    outputWriter.append(stringWriter.getBuffer().toString());
                } finally {
                    outputWriter.close();
                }
            } finally {
                try {
                    stream.close();
                } catch (Exception ex) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, Throwables.render(ex), element);
                }
            }
        } catch (ProcessingException ex) {
            String errorMessage = formatErrorMessage(ex.position(), ex.getMessage());
            errors.add(ElementMessage.of(element, errorMessage));
        } catch (DeclarationException ex) {
            errors.add(ElementMessage.of(element, ex.toString()));
        } catch (IOException ex) {
            errors.add(ElementMessage.of(element, Throwables.render(ex)));
        } catch (RuntimeException ex) {
            errors.add(ElementMessage.of(element, Throwables.render(ex)));
        }
    }
}
class ClassWriter {
    private final CodeWriter codeWriter;
    private final TypeElement element;
    private final TextFileObject templateLoader;
    private final String templateName;
    ClassWriter(CodeWriter compilerManager, TypeElement element, TextFileObject templateLoader, String templateName) {
        this.codeWriter = compilerManager;
        this.element = element;
        this.templateName = templateName;
        this.templateLoader = templateLoader;
    }

    void println(String s) {
        codeWriter.println(s);
    }

    void writeRenderableAdapterClass(String rendererClassSimpleName,
                   TypeElement templateFormatElement, List<String> ifaces) throws IOException, ProcessingException, AnnotatedException {
        String className = element.getQualifiedName().toString();
        PackageElement packageElement = JavaLanguageModel.getInstance().getElements().getPackageOf(element);
        String packageName = packageElement.getQualifiedName().toString();
        TextFormat templateFormatAnnotation = templateFormatElement.getAnnotation(TextFormat.class);
        assert templateFormatAnnotation != null;
        

        List<String> ifaceStrings = new ArrayList<String>(ifaces);
        //ifaces.stream().map(c -> c.getName()).forEach(ifaceStrings::add);
        
        
        String implementsString = ifaceStrings.isEmpty() ? "" : " implements " +
                ifaceStrings.stream().collect(Collectors.joining(", "));
        
        String extendsString = " extends " 
                + Renderable.class.getName() + "<" + templateFormatElement.getQualifiedName() + "," + className + ">";
        
        String rendererImplements = " implements " 
                + Renderer.class.getName()  + "<" + className + ">";
        
        String modifier = element.getModifiers().contains(Modifier.PUBLIC) ? "public " : "";
        
        String adapterClassSimpleName = rendererClassSimpleName + "Definition";

        println("package " + packageName + ";");
        println("// @javax.annotation.Generated(\"" + GenerateRendererProcessor.class.getName() + "\")");
        
        println(modifier + "class " + rendererClassSimpleName + rendererImplements +" {");

        println("    public " + rendererClassSimpleName + "() {" );
        
        println("    }" );
        println("");
        println("    public void render(" + className + " model, Appendable appendable) throws java.io.IOException {");
        println("        new " + adapterClassSimpleName + "(model).render(appendable);");
        println("    }");
        println("");
        println("    public boolean supportsType(Class<?> type) {");
        println("        return " + className + ".class.isAssignableFrom(type);");
        println("    }");
        println("");
        println("    public static " + RenderFunction.class.getName() + " of(" + className + " data) {");
        println("        return new " + adapterClassSimpleName + "(data);");
        println("    }");
        println("}");
        
        String _Appender = Appender.class.getName();
        String _Appendable = Appendable.class.getName();
        String _Formatter = Formatter.class.getName();
        String _RenderService = RenderService.class.getName();

        println("class " + adapterClassSimpleName + extendsString + implementsString +" {");
        println("    public static final String TEMPLATE = \"" + templateName + "\";");
        
        println("    " + _Appender + " appender = " + _RenderService  + ".findService().appender();"   );
        println("    " + _Appender + " escaper = " + templateFormatElement.getQualifiedName() + "." + templateFormatAnnotation.providesMethod() + "();");
        println("    " + _Formatter + " formatter = " + _RenderService + ".findService().formatter" + "();");

        
        println("    private final " + className + " data;");
        String constructorModifier = "protected";
        println("    " + constructorModifier + " " + adapterClassSimpleName + "(" + className + " data) {");
        println("        this.data = data;");
        println("    }");
        
        println("    @Override");
        println("    public String " + "getTemplate() {");
        println("        return TEMPLATE;");
        println("    }");
        
        println("    @Override");
        println("    public " + className  + " getContext() {");
        println("        return this.data;");
        println("    }");
        
        //private static <M> void _render(M model, Appendable appendable, Appender appender, Appender escaper, Formatter formatter);
        println("    @Override");
        println("    protected void " + "doRender("+ _Appendable + " a) throws java.io.IOException {");
        println("        _render(data, a, appender, escaper, formatter);");
        println("    }");
        
        println("    public static " + RenderFunction.class.getName() + " of(" + className + " data) {");
        println("        return new " + adapterClassSimpleName + "(data);");
        println("    }");
        
        writeRendererDefinitionMethod(TemplateCompilerType.SIMPLE);
        println("}");
    }


    

    
    private void writeRendererDefinitionMethod(TemplateCompilerType templateCompilerType ) throws IOException, ProcessingException, AnnotatedException {
        VariableContext variables = VariableContext.createDefaultContext();
        String dataName = variables.introduceNewNameLike("data");
        String className = element.getQualifiedName().toString();
        String _Appender = Appender.class.getName();
        String _Appendable = Appendable.class.getName();
        String _Formatter = Formatter.class.getName();

        //private static <M> void render(M model, Appendable appendable, Appender appender, Appender escaper, Formatter formatter);

        String idt = "\n        ";
        println("    private static void _render(" + className + " " + dataName 
                + idt + ", " + _Appendable + " " + variables.unescapedWriter()
                + idt + ", " + _Appender + " " + variables.appender() 
                + idt + ", " + _Appender + " " + variables.writer()
                + idt + ", " + _Formatter + " " + variables.formatter() 
                + idt + ") throws java.io.IOException {");
        TemplateCompilerContext context = codeWriter.createTemplateContext(templateName, element, dataName, variables);
        codeWriter.compileTemplate(templateLoader, context, templateCompilerType);
        println("");
        println("    }");

    }
    
}
