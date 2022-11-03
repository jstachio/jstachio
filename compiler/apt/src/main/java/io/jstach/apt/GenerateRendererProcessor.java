
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

import io.jstach.apt.GenerateRendererProcessor.RendererModel;
import io.jstach.apt.TemplateCompilerLike.TemplateCompilerType;
import io.jstach.apt.internal.AnnotatedException;
import io.jstach.apt.internal.CodeAppendable;
import io.jstach.apt.internal.FormatterTypes;
import io.jstach.apt.internal.NamedTemplate;
import io.jstach.apt.internal.Position;
import io.jstach.apt.internal.ProcessingConfig;
import io.jstach.apt.internal.ProcessingException;
import io.jstach.apt.internal.ProcessingConfig.PathConfig;
import io.jstach.apt.internal.context.JavaLanguageModel;
import io.jstach.apt.internal.context.RenderingCodeGenerator;
import io.jstach.apt.internal.context.TemplateCompilerContext;
import io.jstach.apt.internal.context.VariableContext;
import io.jstach.apt.internal.meta.ElementMessage;
import io.jstach.apt.internal.util.ClassRef;
import io.jstach.apt.internal.util.Throwables;
import io.jstach.apt.prism.JStacheContentTypePrism;
import io.jstach.apt.prism.JStacheFlagsPrism;
import io.jstach.apt.prism.JStacheFormatterPrism;
import io.jstach.apt.prism.JStacheFormatterTypesPrism;
import io.jstach.apt.prism.JStacheInterfacesPrism;
import io.jstach.apt.prism.JStachePartialPrism;
import io.jstach.apt.prism.JStachePartialsPrism;
import io.jstach.apt.prism.JStachePathPrism;
import io.jstach.apt.prism.JStachePrism;
import io.jstach.apt.prism.Prisms;

import static io.jstach.apt.prism.Prisms.*;

/**
 * Renderer processor
 *
 * @author agentgt
 *
 */
@MetaInfServices(value = Processor.class)
@SupportedAnnotationTypes("*")
public class GenerateRendererProcessor extends AbstractProcessor implements Prisms {

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	Set<ClassRef> rendererClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());

	private static String formatErrorMessage(Position position, @Nullable String message) {
		message = message == null ? "" : message;
		String formatString = "%s:%d: error: %s%n%s%n%s%nsymbol: mustache directive%nlocation: mustache template";
		@Nullable
		Object @NonNull [] fields = new @Nullable Object @NonNull [] { position.fileName(), position.row(), message,
				position.currentLine(), columnPositioningString(position.col()), };
		return String.format(formatString, fields);
	}

	private static String columnPositioningString(int col) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < col - 1; i++)
			builder.append(' ');
		builder.append('^');
		return builder.toString();
	}

	@Override
	public @NonNull Set<@NonNull String> getSupportedAnnotationTypes() {
		return Set.copyOf(Prisms.ANNOTATIONS);
	}

	private final List<ElementMessage> errors = new ArrayList<ElementMessage>();

	@Override
	public boolean process(Set<? extends TypeElement> processEnnotations, RoundEnvironment roundEnv) {
		try {
			return _process(processEnnotations, roundEnv);
		}
		catch (AnnotatedException e) {
			e.report(processingEnv.getMessager());
			return true;
		}
	}

	private boolean _process(Set<? extends TypeElement> processEnnotations, RoundEnvironment roundEnv)
			throws AnnotatedException {
		/*
		 * Lets just bind the damn utils so that we do not have to pass them around
		 * everywhere
		 */
		JavaLanguageModel.createInstance(processingEnv.getTypeUtils(), processingEnv.getElementUtils(),
				processingEnv.getMessager());
		if (roundEnv.processingOver()) {
			for (ElementMessage error : errors) {
				TypeElement element = processingEnv.getElementUtils().getTypeElement(error.qualifiedElementName());
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, error.message(), element);
			}
			ClassRef serviceClass = ClassRef.ofBinaryName(RENDERER_PROVIDER_CLASS);
			ServicesFiles.writeServicesFile(processingEnv.getFiler(), processingEnv.getMessager(), serviceClass,
					rendererClasses);
			return false;
		}
		else {
			TypeElement jstacheElement = processingEnv.getElementUtils().getTypeElement(JSTACHE_CLASS);
			for (Element element : roundEnv.getElementsAnnotatedWith(jstacheElement)) {
				TypeElement classElement = (TypeElement) element;
				List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
				AnnotationMirror directive = null;
				for (AnnotationMirror annotationMirror : annotationMirrors) {
					if (processingEnv.getTypeUtils().isSubtype(annotationMirror.getAnnotationType(),
							jstacheElement.asType()))
						directive = annotationMirror;
				}
				assert directive != null;
				ClassRef ref = writeRenderableAdapterClass(classElement, directive);
				if (ref != null) {
					rendererClasses.add(ref);
				}
			}
			TypeElement jstachesElement = processingEnv.getElementUtils().getTypeElement(JSTACHES_CLASS);
			for (Element element : roundEnv.getElementsAnnotatedWith(jstachesElement)) {
				TypeElement classElement = (TypeElement) element;
				List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
				for (AnnotationMirror mirror : annotationMirrors) {
					if (processingEnv.getTypeUtils().isSubtype(mirror.getAnnotationType(), jstachesElement.asType())) {
						Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = mirror
								.getElementValues();
						for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues
								.entrySet()) {
							if (entry.getKey().getSimpleName().contentEquals("value")) {
								@SuppressWarnings("unchecked")
								List<? extends AnnotationValue> directives = (List<? extends AnnotationValue>) entry
										.getValue().getValue();
								for (AnnotationValue directiveValue : directives) {
									AnnotationMirror directive = (AnnotationMirror) directiveValue.getValue();
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
			return true;
		}
	}

	private PathConfig resolvePathConfig(TypeElement element) {
		JStachePathPrism prism = JStachePathPrism.getInstanceOn(element);
		if (prism == null) {
			PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
			prism = JStachePathPrism.getInstanceOn(packageElement);
		}
		if (prism == null) {
			return new PathConfig("", "");
		}
		return new PathConfig(prism.prefix(), prism.suffix());
	}

	private List<String> resolveBaseInterfaces(TypeElement element) throws AnnotatedException {
		PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
		JStacheInterfacesPrism prism = JStacheInterfacesPrism.getInstanceOn(packageElement);
		if (prism != null) {

			var modelImplements = prism.modelImplements();
			assert modelImplements != null;
			for (TypeMirror mi : modelImplements) {
				if (!JavaLanguageModel.getInstance().isSubtype(element.asType(), mi)) {
					throw new AnnotatedException(element, "per package declaration of @" + JSTACHE_INTERFACES_CLASS
							+ " model required to implement " + mi.toString());
				}
			}
			var ri = prism.rendererImplements();
			assert ri != null;
			return ri.stream().map(tm -> getTypeName(tm)).toList();
		}
		return List.of();
	}

	private Map<String, NamedTemplate> resolvePartials(TypeElement element) {

		Map<String, NamedTemplate> paths = new LinkedHashMap<>();
		var prism = JStachePartialsPrism.getInstanceOn(element);
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

	private static NamedTemplate resolveNamedTemplate(@Nullable String name, @Nullable String path,
			@Nullable String template) {
		NamedTemplate nt;
		assert name != null;
		if (path != null && !path.isBlank()) {
			nt = new NamedTemplate.FileTemplate(name, path);
		}
		else if (template != null && !template.equals("__NOT_SET__")) {
			nt = new NamedTemplate.InlineTemplate(name, template);
		}
		else {
			nt = new NamedTemplate.FileTemplate(name, name);

		}
		return nt;
	}

	private Set<Flag> resolveFlags(TypeElement element) {
		var prism = JStacheFlagsPrism.getInstanceOn(element);
		var flags = EnumSet.noneOf(Flag.class);
		if (prism != null) {
			prism.flags().stream().map(Flag::valueOf).forEach(flags::add);
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

	record RendererModel( //
			TypeElement element, //
			ClassRef rendererClassRef, //
			String path, //
			PathConfig pathConfig, //
			String template, //
			Charset charset, //
			TypeElement contentTypeElement, //
			FormatterTypes formatterTypes, //
			TypeElement formatterTypeElement, //
			Map<String, NamedTemplate> partials, //
			List<String> ifaces, //
			Set<Flag> flags) implements ProcessingConfig {

		public NamedTemplate namedTemplate() {
			String name = element.getQualifiedName().toString() + ".mustache";
			String path = path();
			String template = null;
			if (!path.isBlank()) {
				name = path;
			}
			if (!template().isBlank()) {
				template = template();
			}
			return resolveNamedTemplate(name, path, template);

		}

	}

	private RendererModel model(TypeElement element, AnnotationMirror directiveMirror)
			throws DeclarationException, AnnotatedException, DeclarationException {

		if (!element.getTypeParameters().isEmpty()) {
			throw new DeclarationException(
					"Can't generate renderer for class with type variables: " + element.getQualifiedName());
		}

		JStachePrism gp = JStachePrism.getInstance(directiveMirror);

		if (gp == null) {
			throw new AnnotatedException(element, "Missing annotation. bug.");
		}

		TypeElement contentTypeElement = resolveContentType(gp);
		TypeElement formatterElement = resolveFormatter(element, gp);
		Charset charset = gp.charset().equals(":default") ? Charset.defaultCharset() : Charset.forName(gp.charset());
		String path = gp.path();
		PathConfig pathConfig = resolvePathConfig(element);
		String template = gp.template();
		assert template != null;
		List<String> ifaces = resolveBaseInterfaces(element);
		ClassRef rendererClassRef = resolveRendererClassRef(element, gp);
		FormatterTypes formatterTypes = resolveFormatterTypes(element);
		Map<String, NamedTemplate> partials = resolvePartials(element);
		Set<Flag> flags = resolveFlags(element);

		var model = new RendererModel( //
				element, //
				rendererClassRef, //
				path, //
				pathConfig, //
				template, //
				charset, //
				contentTypeElement, //
				formatterTypes, //
				formatterElement, //
				partials, //
				ifaces, //
				flags);
		return model;
	}

	private ClassRef resolveRendererClassRef(TypeElement element, JStachePrism gp) {
		String rendererClassSimpleName = resolveAdapterName(element, gp);
		PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
		assert packageElement != null;
		ClassRef rendererClassRef = ClassRef.of(packageElement, rendererClassSimpleName);
		return rendererClassRef;
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

		JStacheContentTypePrism contentTypePrism = JStacheContentTypePrism.getInstanceOn(templateFormatElement);
		if (contentTypePrism == null) {
			throw new DeclarationException(templateFormatElement.getQualifiedName()
					+ " class is used as a template content type, but not marked with " + JSTACHE_CONTENT_TYPE_CLASS
					+ " annotation");
		}

		/*
		 * TODO clean this up to resolve format
		 */
		var autoFormatElement = JavaLanguageModel.getInstance().getElements().getTypeElement(AUTO_CONTENT_TYPE_CLASS);
		if (JavaLanguageModel.getInstance().isSameType(autoFormatElement.asType(), templateFormatElement.asType())) {
			templateFormatElement = JavaLanguageModel.getInstance().getElements().getTypeElement(HTML_CLASS);
			if (templateFormatElement == null) {
				throw new DeclarationException("Missing default TextFormat class of Html");
			}
		}
		return templateFormatElement;
	}

	private TypeElement resolveFormatter(TypeElement element, JStachePrism gp) throws DeclarationException {

		PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
		JStacheFormatterTypesPrism formatterTypes = JStacheFormatterTypesPrism.getInstanceOn(packageElement);

		var lm = JavaLanguageModel.getInstance();
		TypeMirror templateFormatType = gp.formatter();

		TypeElement formatElement = formatterElement(templateFormatType);

		TypeElement autoFormatElement = lm.getElements().getTypeElement(AUTO_FORMATTER_CLASS);

		if (formatterTypes != null && lm.isSameType(autoFormatElement.asType(), formatElement.asType())) {
			formatElement = formatterElement(formatterTypes.formatter());
		}

		if (lm.isSameType(autoFormatElement.asType(), formatElement.asType())) {
			formatElement = lm.getElements().getTypeElement(DEFAULT_FORMATTER_CLASS);
		}
		return formatElement;
	}

	private TypeElement formatterElement(TypeMirror templateFormatType) throws DeclarationException {
		TypeElement formatElement = null;
		if (templateFormatType instanceof DeclaredType dt) {
			formatElement = (TypeElement) dt.asElement();
		}
		else {
			throw new ClassCastException("Expecting DeclaredType for formatter " + templateFormatType);
		}

		JStacheFormatterPrism formatterPrism = JStacheFormatterPrism.getInstanceOn(formatElement);
		if (formatterPrism == null) {
			throw new DeclarationException(formatElement.getQualifiedName()
					+ " class is used as a formatter, but not marked with " + JSTACHE_FORMATTER_CLASS + " annotation");
		}
		return formatElement;
	}

	private String resolveAdapterName(TypeElement element, JStachePrism gp) {
		String directiveAdapterName = null;
		directiveAdapterName = gp.adapterName();
		String adapterClassSimpleName;
		if (!directiveAdapterName.equals(":auto")) {
			adapterClassSimpleName = directiveAdapterName;
		}
		else {
			ClassRef ref = ClassRef.of(element);
			adapterClassSimpleName = ref.getSimpleName() + IMPLEMENTATION_SUFFIX;
		}
		return adapterClassSimpleName;
	}

	private @Nullable ClassRef writeRenderableAdapterClass(TypeElement element, AnnotationMirror directiveMirror)
			throws AnnotatedException {

		try {
			var model = model(element, directiveMirror);
			ProcessingConfig config = model;
			StringWriter stringWriter = new StringWriter();
			try (SwitchablePrintWriter switchablePrintWriter = SwitchablePrintWriter.createInstance(stringWriter)) {
				TextFileObject templateResource = new TextFileObject(config, Objects.requireNonNull(processingEnv));
				JavaLanguageModel javaModel = JavaLanguageModel.getInstance();
				RenderingCodeGenerator codeGenerator = RenderingCodeGenerator.createInstance(javaModel,
						model.formatterTypes());
				CodeWriter codeWriter = new CodeWriter(switchablePrintWriter, codeGenerator, model.partials(), config);
				ClassWriter writer = new ClassWriter(codeWriter, templateResource);

				writer.writeRenderableAdapterClass(model);
			}

			JavaFileObject sourceFile = processingEnv.getFiler()
					.createSourceFile(model.rendererClassRef().requireCanonicalName(), element);
			OutputStream stream = sourceFile.openOutputStream();
			try {
				Writer outputWriter = new OutputStreamWriter(stream, Charset.defaultCharset());
				try {
					outputWriter.append(stringWriter.getBuffer().toString());
				}
				finally {
					outputWriter.close();
				}
			}
			finally {
				try {
					stream.close();
				}
				catch (Exception ex) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, Throwables.render(ex), element);
				}
			}
			return model.rendererClassRef();
		}
		catch (ProcessingException ex) {
			String errorMessage = formatErrorMessage(ex.position(), ex.getMessage());
			errors.add(ElementMessage.of(element, errorMessage));
		}
		catch (DeclarationException ex) {
			errors.add(ElementMessage.of(element, ex.toString()));
		}
		catch (IOException ex) {
			errors.add(ElementMessage.of(element, Throwables.render(ex)));
		}
		catch (RuntimeException ex) {
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
		var contentTypeElement = model.contentTypeElement();
		var formatterTypeElement = model.formatterTypeElement();
		var ifaces = model.ifaces();
		var renderClassRef = model.rendererClassRef();
		ClassRef modelClassRef = ClassRef.of(element);
		String className = modelClassRef.getCanonicalName();
		if (className == null) {
			throw new AnnotatedException(element, "Anonymous classes can not be used as models");
		}
		String packageName = modelClassRef.getPackageName();
		/*
		 * TODO we should make this whole "provides" pattern DRY
		 */
		JStacheContentTypePrism contentTypePrism = JStacheContentTypePrism.getInstanceOn(contentTypeElement);
		assert contentTypePrism != null;
		JStacheFormatterPrism formatterPrism = JStacheFormatterPrism.getInstanceOn(formatterTypeElement);
		assert formatterPrism != null;

		String implementsString = ifaces.isEmpty() ? "" : ", " + ifaces.stream().collect(Collectors.joining(", "));

		String rendererImplements = " implements " + RENDERER_CLASS + "<" + className + ">, " + TEMPLATE_INFO_CLASS
				+ ", " + RENDERER_PROVIDER_CLASS + implementsString;

		String modifier = element.getModifiers().contains(Modifier.PUBLIC) ? "public " : "";

		String rendererClassSimpleName = renderClassRef.getSimpleName();

		NamedTemplate namedTemplate = model.namedTemplate();

		String templateName = namedTemplate.name();
		String templatePath = model.pathConfig().resolveTemplatePath(model.path());
		String templateString = namedTemplate.template();

		String templateStringJava = CodeAppendable.stringConcat(templateString);

		String _Appendable = Appendable.class.getName();
		String _Appender = APPENDER_CLASS + "<" + _Appendable + ">";

		String _Escaper = ESCAPER_CLASS;
		String _Formatter = FORMATTER_CLASS;

		String idt = "\n        ";

		println("package " + packageName + ";");
		println("");
		println("/**");
		println(" * Generated Renderer.");
		println(" */");
		println("// @javax.annotation.Generated(\"" + GenerateRendererProcessor.class.getName() + "\")");

		println(modifier + "class " + rendererClassSimpleName + rendererImplements + " {");

		println("    /**");
		println("     * @hidden");
		println("     */");
		println("    public static final String TEMPLATE_PATH = \"" + templatePath + "\";");
		println("");
		println("    /**");
		println("     * @hidden");
		println("     */");
		println("");
		println("    public static final String TEMPLATE_STRING = " + templateStringJava + ";");
		println("");
		println("    /**");
		println("     * @hidden");
		println("     */");
		println("    public static final String TEMPLATE_NAME = \"" + templateName + "\";");

		println("    /**");
		println("     * Generated Renderer.");
		println("     */");
		println("    public " + rendererClassSimpleName + "() {");
		println("    }");
		println("");
		println("    @Override");
		println("    public void render(" + className + " model, Appendable a) throws java.io.IOException {");
		println("        render(model, a, templateFormatter(), templateEscaper());");
		println("    }");
		println("");
		println("    @Override");
		println("    public void render(" //
				+ idt + className + " model, " //
				+ idt + _Appendable + " a, " //
				+ idt + _Formatter + " formatter" + "," //
				+ idt + _Escaper + " escaper" + ") throws java.io.IOException {");
		println("        execute(model, a, formatter, escaper, templateAppender());");
		println("    }");

		println("");
		println("    @Override");
		println("    public boolean supportsType(Class<?> type) {");
		println("        return " + className + ".class.isAssignableFrom(type);");
		println("    }");
		println("");
		println("    @Override");
		println("    public java.util.List<" + RENDERER_CLASS + "<?>> " + "provideRenderers() {");
		println("        return java.util.List.of(this);");
		println("    }");
		println("");
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
		println("    public Class<?> " + "templateContentType() {");
		println("        return " + contentTypeElement.getQualifiedName() + ".class;");
		println("    }");

		println("    @Override");
		println("    public  " + _Escaper + " templateEscaper() {");
		println("        return " + _Escaper + ".of(" //
				+ contentTypeElement.getQualifiedName() + "." + contentTypePrism.providesMethod() + "());");
		println("    }");

		println("    @Override");
		println("    public " + _Formatter + " templateFormatter() {");
		println("        return " + _Formatter + ".of(" //
				+ formatterTypeElement.getQualifiedName() + "." + formatterPrism.providesMethod() + "());");
		println("    }");
		println("");
		println("    /**");
		println("     * Appender.");
		println("     * @return appender for writing unescaped variables.");
		println("     */");
		println("    public " + _Appender + " templateAppender() {");
		println("        return " + APPENDER_CLASS + ".defaultAppender();");
		println("    }");
		println("");
		println("    /**");
		println("     * Convience static factory.");
		println("     * @return renderer same as calling no-arg constructor");
		println("     */");
		println("    public static " + rendererClassSimpleName + " of() {");
		println("        return new " + rendererClassSimpleName + "();");
		println("    }");
		println("");
		println("    /**");
		println("     * Convience static factory to bind a model.");
		println("     * @param data model");
		println("     * @return render function");
		println("     */");
		println("    public static " + RENDER_FUNCTION_CLASS + " of(" + className + " data) {");
		println("        return a -> of().render(data, a);");
		println("    }");

		println("");
		writeRendererDefinitionMethod(TemplateCompilerType.SIMPLE, model);
		println("}");
	}

	private void writeRendererDefinitionMethod(TemplateCompilerType templateCompilerType, RendererModel model)
			throws IOException, ProcessingException, AnnotatedException {
		var element = model.element();
		VariableContext variables = VariableContext.createDefaultContext();
		String dataName = variables.introduceNewNameLike("data");
		String className = element.getQualifiedName().toString();
		String _Appender = APPENDER_CLASS;
		String _Appendable = Appendable.class.getName();
		String _Formatter = FORMATTER_CLASS;

		String _A = "<A extends " + _Appendable + ">";

		String idt = "\n        ";

		println("    /**");
		println("     * Renders the passed in model.");
		println("     * @param <A> appendable type.");
		println("     * @param " + dataName + " model");
		println("     * @param " + variables.unescapedWriter() + " appendable to write to.");
		println("     * @param " + variables.formatter() + " formats variables before they are passed to the escaper.");
		println("     * @param " + variables.escaper() + " used to write escaped variables.");
		println("     * @param " + variables.appender() + " used to write unescaped variables.");
		println("     * @throws java.io.IOException if an error occurs while writing to the appendable");
		println("     */");
		println("    public static " + _A + " void execute(" //
				+ idt + className + " " + dataName + ", " //
				+ idt + "A" + " " + variables.unescapedWriter() + "," //
				+ idt + _Formatter + " " + variables.formatter() + "," //
				+ idt + _Appender + "<? super A> " + variables.escaper() + "," //
				+ idt + _Appender + "<A> " + variables.appender() + ") throws java.io.IOException {");
		TemplateCompilerContext context = codeWriter.createTemplateContext(model.namedTemplate(), element, dataName,
				variables, model.flags());
		codeWriter.compileTemplate(templateLoader, context, templateCompilerType);
		println("");
		println("    }");

	}

}
