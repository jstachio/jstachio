
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
import java.util.concurrent.ConcurrentHashMap;
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
import io.jstach.Formatter;
import io.jstach.RenderFunction;
import io.jstach.Renderable;
import io.jstach.Renderer;
import io.jstach.annotation.JStache;
import io.jstach.annotation.JStacheContentType;
import io.jstach.annotation.JStacheFlags;
import io.jstach.annotation.JStacheFlags.Flag;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStaches;
import io.jstach.apt.GenerateRendererProcessor.RendererModel;
import io.jstach.apt.TemplateCompilerLike.TemplateCompilerType;
import io.jstach.apt.context.JavaLanguageModel;
import io.jstach.apt.context.RenderingCodeGenerator;
import io.jstach.apt.context.TemplateCompilerContext;
import io.jstach.apt.context.VariableContext;
import io.jstach.apt.meta.ElementMessage;
import io.jstach.apt.prism.JStacheBasePathPrism;
import io.jstach.apt.prism.JStacheFlagsPrism;
import io.jstach.apt.prism.JStacheFormatterTypesPrism;
import io.jstach.apt.prism.JStacheInterfacesPrism;
import io.jstach.apt.prism.JStachePartialMappingPrism;
import io.jstach.apt.prism.JStachePartialPrism;
import io.jstach.apt.prism.JStachePrism;
import io.jstach.escapers.Html;
import io.jstach.spi.JStacheServices;

@MetaInfServices(value=Processor.class)
@SupportedAnnotationTypes("*")
public class GenerateRendererProcessor extends AbstractProcessor {

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    Set<ClassRef> rendererClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());
    
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
            ClassRef serviceClass = ClassRef.of(Renderer.class);
            ServicesFiles.writeServicesFile(processingEnv.getFiler(), processingEnv.getMessager(), serviceClass, rendererClasses);
        } else {
            /*
             * Lets just bind the damn utils so that we do not have to pass them around everywhere
             */
            JavaLanguageModel.createInstance(processingEnv.getTypeUtils(), processingEnv.getElementUtils(), processingEnv.getMessager());
            Element generateRenderableAdapterElement = processingEnv.getElementUtils().getTypeElement(JStache.class.getName());
            for (Element element: roundEnv.getElementsAnnotatedWith(JStache.class)) {
                TypeElement classElement = (TypeElement)element;
                List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
                AnnotationMirror directive = null;
                for (AnnotationMirror annotationMirror: annotationMirrors) {
                    if (processingEnv.getTypeUtils().isSubtype(annotationMirror.getAnnotationType(), generateRenderableAdapterElement.asType()))
                        directive = annotationMirror;
                }
                assert directive != null;
                ClassRef ref = writeRenderableAdapterClass(classElement, directive);
                if (ref != null) {
                    rendererClasses.add(ref);
                }
            }
            Element generateRenderableAdaptersElement = processingEnv.getElementUtils().getTypeElement(JStaches.class.getName());
            for (Element element: roundEnv.getElementsAnnotatedWith(JStaches.class)) {
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
                                    ClassRef ref = writeRenderableAdapterClass(classElement, directive);
                                    if (ref != null) {
                                        rendererClasses.add(ref);
                                    }
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
        JStacheBasePathPrism prism = JStacheBasePathPrism.getInstanceOn(packageElement);
        if (prism == null) {
            return "";
        }
        String basePath = prism.value();
        if (basePath.equals("")) {
            basePath = packageElement.getQualifiedName().toString().replace(".", "/") + "/";
        }
        return basePath;
    }
    
    private List<String> resolveBaseInterfaces(TypeElement element) {
        PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
        JStacheInterfacesPrism prism = JStacheInterfacesPrism.getInstanceOn(packageElement);
        if (prism != null) {
            var tm =  prism.value();
            assert tm != null;
            return List.of(getTypeName(tm));
        }
        return List.of();
    }
    
    private Map<String, NamedTemplate> resolvePartials(TypeElement element) {

        Map<String, NamedTemplate> paths = new LinkedHashMap<>();
        var prism = JStachePartialMappingPrism.getInstanceOn(element);
        if (prism != null) {
            var tps = prism.value();
            for (JStachePartialPrism tp : tps) {
                NamedTemplate nt;
                String path = tp.path();
                String name = tp.name();
                assert name != null;
                String template = tp.template();
                
                nt = resolveNamedTemplate(name, path, template);
                paths.put(name, nt);
            }
        }
        return paths;
    }

    private static NamedTemplate resolveNamedTemplate(@Nullable String name, @Nullable String path, @Nullable String template) {
        NamedTemplate nt;
        assert name != null;
        if (path != null && ! path.isBlank() ) {
            nt = new NamedTemplate.FileTemplate(name, path);
        }
        else if (template != null &&  ! template.equals(JStachePartial.NOT_SET)) {
            nt = new NamedTemplate.InlineTemplate(name, template);
        }
        else {
            nt = new NamedTemplate.FileTemplate(name, name);

        }
        return nt;
    }
    
    private Set<JStacheFlags.Flag> resolveFlags(TypeElement element) {
        var prism = JStacheFlagsPrism.getInstanceOn(element);
        var flags = EnumSet.noneOf(JStacheFlags.Flag.class);
        if (prism != null) {
            prism.flags().stream().map(JStacheFlags.Flag::valueOf).forEach(flags::add);
        }
        return Collections.unmodifiableSet(flags);
    }

    private String getTypeName(TypeMirror tm) {
       var e = ((DeclaredType) tm).asElement();
       var te = (TypeElement) e;
       return te.getQualifiedName().toString();
    }
    
    private FormatterTypes resolveFormatterTypes(TypeElement element) {
        PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
        JStacheFormatterTypesPrism prism = JStacheFormatterTypesPrism.getInstanceOn(packageElement);
        if (prism == null) {
            return FormatterTypes.acceptOnlyKnownTypes();
        }
        List<String> classNames = prism.types().stream().map(tm -> getTypeName(tm)).toList();
        List<String> patterns = prism.patterns().stream().toList();
        return new FormatterTypes.ConfiguredFormatterTypes(classNames, patterns);
    }
    
    record RendererModel(
            TypeElement element, 
            ClassRef rendererClassRef, 
            String path,
            String template,
            Charset charset,
            TypeElement contentTypeElement,
            FormatterTypes formatterTypes, 
            Map<String, NamedTemplate> partials, 
            List<String> ifaces, 
            Set<Flag> flags) {
        
        public NamedTemplate namedTemplate() {
            String name = element.getQualifiedName().toString() + ".mustache";
            String path = path();
            String template = null;
            if (! path.isBlank()) {
                name = path;
            }
            if (! template().isBlank()) {
                template = template();
            }
            return resolveNamedTemplate(name, path, template);
            
        }
        
    }

    private RendererModel model(TypeElement element, AnnotationMirror directiveMirror)
            throws DeclarationException, AnnotatedException, DeclarationException {
        
        if (!element.getTypeParameters().isEmpty()) {
            throw new DeclarationException("Can't generate renderer for class with type variables: " + element.getQualifiedName());
        }
        
        JStachePrism gp = JStachePrism.getInstance(directiveMirror);
        
        if (gp == null) {
            throw new AnnotatedException(element, "Missing annotation. bug.");
        }

        TypeElement contentTypeElement = resolveContentType(gp);
        Charset charset = gp.charset().equals(":default") ? Charset.defaultCharset() : Charset.forName(gp.charset());
        String path = resolveTemplatePath(element, gp);
        String template =  gp.template();
        assert template != null;
        List<String> ifaces = resolveBaseInterfaces(element);
        ClassRef rendererClassRef = resolveRendererClassRef(element, gp);
        FormatterTypes formatterTypes = resolveFormatterTypes(element);
        Map<String, NamedTemplate> partials = resolvePartials(element);
        Set<JStacheFlags.Flag> flags = resolveFlags(element);
        
        var model = new RendererModel(element, rendererClassRef, path, template, charset, contentTypeElement, formatterTypes, partials, ifaces, flags);
        return model;
    }


    private ClassRef resolveRendererClassRef(TypeElement element, JStachePrism gp) {
        String rendererClassSimpleName = resolveAdapterName(element, gp);
        PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
        assert packageElement != null;
        ClassRef rendererClassRef = ClassRef.of(packageElement, rendererClassSimpleName);
        return rendererClassRef;
    }

    private String resolveTemplatePath(TypeElement element, JStachePrism gp) {
        String templatePath = null;
        templatePath = gp.path();
        String basePath = resolveBasePath(element);
        if (! templatePath.isBlank() &&  ! templatePath.startsWith("/")) {
            templatePath = basePath + templatePath;
        }
        return templatePath;
    }

    private TypeElement resolveContentType(JStachePrism gp) throws DeclarationException {
        TypeElement templateFormatElement = null;
        TypeMirror templateFormatType = gp.contentType();
        if (templateFormatType instanceof DeclaredType dt) {
            templateFormatElement = (TypeElement) dt.asElement();
        }
        else {
            throw new ClassCastException("Expecting DeclaredType for contentType " + gp.contentType());
        }
        @Nullable JStacheContentType templateFormatAnnotation = templateFormatElement.getAnnotation(JStacheContentType.class);
        if (templateFormatAnnotation == null) {
            throw new DeclarationException(templateFormatElement.getQualifiedName() + " class is used as a template content type, but not marked with " + JStacheContentType.class.getName() + " annotation");
        }
        
        /*
         * TODO clean this up to resolve format
         */
        var autoFormatElement = JavaLanguageModel.getInstance().getElements().getTypeElement(JStache.AutoContentType.class.getCanonicalName());
        if( JavaLanguageModel.getInstance().isSameType(autoFormatElement.asType(), templateFormatElement.asType())) {
            templateFormatElement = JavaLanguageModel.getInstance().getElements().getTypeElement(Html.class.getCanonicalName());
            if (templateFormatElement == null) {
                throw new DeclarationException("Missing default TextFormat class of Html");
            }
        }
        return templateFormatElement;
    }

    private String resolveAdapterName(TypeElement element, JStachePrism gp) {
        String directiveAdapterName = null;
        directiveAdapterName = gp.adapterName();
        String adapterClassSimpleName;
        if (!directiveAdapterName.equals(":auto"))
            adapterClassSimpleName = directiveAdapterName;
        else {
            ClassRef ref = ClassRef.of(element);
            adapterClassSimpleName = ref.getSimpleName() + "Renderer";    //ref.getBinaryNameMinusPackage().replace("$", "_") + "Renderer";
        }
        return adapterClassSimpleName;
    }
    
    private @Nullable ClassRef writeRenderableAdapterClass(TypeElement element, AnnotationMirror directiveMirror) throws AnnotatedException {
        
        try {
            var model = model(element, directiveMirror);
            StringWriter stringWriter = new StringWriter();
            try (SwitchablePrintWriter switchablePrintWriter = SwitchablePrintWriter.createInstance(stringWriter)){
                TextFileObject templateResource = new TextFileObject(Objects.requireNonNull(processingEnv), model.charset());
                JavaLanguageModel javaModel = JavaLanguageModel.getInstance();
                RenderingCodeGenerator codeGenerator = RenderingCodeGenerator.createInstance(javaModel, model.formatterTypes());
                CodeWriter codeWriter = new CodeWriter(switchablePrintWriter, codeGenerator, model.partials(), model.flags());
                ClassWriter writer = new ClassWriter(codeWriter, templateResource);

                writer.writeRenderableAdapterClass(model);
            }
            
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(model.rendererClassRef().requireCanonicalName(), element);
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
            return model.rendererClassRef();
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
        return null;
    }
}
class ClassWriter {
    private final CodeWriter codeWriter;
    private final TextFileObject templateLoader;
    ClassWriter(CodeWriter compilerManager, TextFileObject templateLoader) {
        this.codeWriter = compilerManager;
        this.templateLoader = templateLoader;
    }

    void println(String s) {
        codeWriter.println(s);
    }

    void writeRenderableAdapterClass(RendererModel model) throws IOException, ProcessingException, AnnotatedException {
        var element = model.element();
        var templateFormatElement = model.contentTypeElement();
        var ifaces = model.ifaces();
        var renderClassRef = model.rendererClassRef();
        ClassRef modelClassRef = ClassRef.of(element);
        String className = modelClassRef.getCanonicalName();
        if (className == null) {
            throw new AnnotatedException(element, "Anonymous classes can not be used as models");
        }
        String packageName = modelClassRef.getPackageName();
        JStacheContentType templateFormatAnnotation = templateFormatElement.getAnnotation(JStacheContentType.class);
        assert templateFormatAnnotation != null;
        
        String implementsString = ifaces.isEmpty() ? "" : " implements " +
                ifaces.stream().collect(Collectors.joining(", "));
        
        String extendsString = " extends " 
                + Renderable.class.getName() + "<" + templateFormatElement.getQualifiedName() + "," + className + ">";
        
        String rendererImplements = " implements " 
                + Renderer.class.getName()  + "<" + className + ">";
        
        String modifier = element.getModifiers().contains(Modifier.PUBLIC) ? "public " : "";
        
        String rendererClassSimpleName = renderClassRef.getSimpleName();
        
        String adapterClassSimpleName = rendererClassSimpleName + "Definition";
        
        NamedTemplate namedTemplate = model.namedTemplate();
        
        String templateName = namedTemplate.name();
        String templatePath = namedTemplate.path();
        String templateString = namedTemplate.template();
        
        String templateStringJava = CodeAppendable.stringConcat(templateString);

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
        String _RenderService = JStacheServices.class.getName();

        println("class " + adapterClassSimpleName + extendsString + implementsString +" {");
        println("    public static final String TEMPLATE_PATH = \"" + templatePath  + "\";");
        println("    public static final String TEMPLATE_STRING = " + templateStringJava + ";");
        println("    public static final String TEMPLATE_NAME = \"" + templateName + "\";");


        
        println("    " + _Appender + " appender = " + _RenderService  + ".findService().appender();"   );
        println("    " + _Appender + " escaper = " + templateFormatElement.getQualifiedName() + "." + templateFormatAnnotation.providesMethod() + "();");
        println("    " + _Formatter + " formatter = " + _RenderService + ".findService().formatter" + "();");

        
        println("    private final " + className + " data;");
        String constructorModifier = "protected";
        println("    " + constructorModifier + " " + adapterClassSimpleName + "(" + className + " data) {");
        println("        this.data = data;");
        println("    }");
        
        println("    @Override");
        println("    public String " + "templatePath() {");
        println("        return TEMPLATE_PATH;");
        println("    }");
        
        println("    @Override");
        println("    public String " + "templateName() {");
        println("        return TEMPLATE_NAME;");
        println("    }");
        
        println("    @Override");
        println("    public String " + "templateString() {");
        println("        return TEMPLATE_STRING;");
        println("    }");
        
        println("    @Override");
        println("    public " + className  + " getContext() {");
        println("        return this.data;");
        println("    }");
        
        //private static <M> void _render(M model, Appendable appendable, Appender appender, Appender escaper, Formatter formatter);
        println("    @Override");
        println("    protected void " + "doRender("+ _Appendable + " a) throws java.io.IOException {");
        println("        render(data, a, appender, escaper, formatter);");
        println("    }");
        
        println("    public static " + RenderFunction.class.getName() + " of(" + className + " data) {");
        println("        return new " + adapterClassSimpleName + "(data);");
        println("    }");
        
        writeRendererDefinitionMethod(TemplateCompilerType.SIMPLE, model);
        println("}");
    }


    
    private void writeRendererDefinitionMethod(TemplateCompilerType templateCompilerType, RendererModel model ) throws IOException, ProcessingException, AnnotatedException {
        var element = model.element();
        VariableContext variables = VariableContext.createDefaultContext();
        String dataName = variables.introduceNewNameLike("data");
        String className = element.getQualifiedName().toString();
        String _Appender = Appender.class.getName();
        String _Appendable = Appendable.class.getName();
        String _Formatter = Formatter.class.getName();
        
        String generic = "<A extends " + _Appendable + ">";

        String idt = "\n        ";
        println("    public static " + generic + " void render(" + className + " " + dataName 
                + idt + ", " + "A" + " " + variables.unescapedWriter()
                + idt + ", " + _Appender + "<A> " + variables.appender() 
                + idt + ", " + _Appender + "<? super A> " + variables.writer()
                + idt + ", " + _Formatter + " " + variables.formatter() 
                + idt + ") throws java.io.IOException {");
        TemplateCompilerContext context = codeWriter.createTemplateContext(model.namedTemplate(), element, dataName, variables, model.flags());
        codeWriter.compileTemplate(templateLoader, context, templateCompilerType);
        println("");
        println("    }");

    }
    
}
