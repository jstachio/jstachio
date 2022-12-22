
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterators.AbstractSpliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.kohsuke.MetaInfServices;

import io.jstach.apt.internal.AnnotatedException;
import io.jstach.apt.internal.FormatterTypes;
import io.jstach.apt.internal.FormatterTypes.FormatCallType;
import io.jstach.apt.internal.NamedTemplate;
import io.jstach.apt.internal.Position;
import io.jstach.apt.internal.ProcessingConfig;
import io.jstach.apt.internal.ProcessingConfig.PathConfig;
import io.jstach.apt.internal.ProcessingException;
import io.jstach.apt.internal.context.JavaLanguageModel;
import io.jstach.apt.internal.context.RenderingCodeGenerator;
import io.jstach.apt.internal.meta.ElementMessage;
import io.jstach.apt.internal.util.ClassRef;
import io.jstach.apt.internal.util.Throwables;
import io.jstach.apt.internal.util.Throwables.SneakyFunction;
import io.jstach.apt.prism.JStacheConfigPrism;
import io.jstach.apt.prism.JStacheConfigPrism.JStacheName;
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
		Map<String, String> options = processingEnv.getOptions();

		if (roundEnv.processingOver()) {
			for (ElementMessage error : errors) {
				TypeElement element = processingEnv.getElementUtils().getTypeElement(error.qualifiedElementName());
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, error.message(), element);
			}
			ClassRef serviceClass = ClassRef.ofBinaryName(TEMPLATE_PROVIDER_CLASS);
			ServicesFiles.writeServicesFile(processingEnv.getFiler(), processingEnv.getMessager(), serviceClass,
					rendererClasses);
			return false;
		}
		else {
			TypeElement jstacheElement = processingEnv.getElementUtils().getTypeElement(JSTACHE_CLASS);
			for (Element element : roundEnv.getElementsAnnotatedWith(jstacheElement)) {
				TypeElement classElement = (TypeElement) element;
				JStachePrism jstache = JStachePrism.getInstanceOn(classElement);
				ClassRef ref = writeRenderableAdapterClass(classElement, jstache, options);
				if (ref != null) {
					rendererClasses.add(ref);
				}
			}
			return true;
		}
	}

	private PathConfig resolvePathConfig(TypeElement element) {
		JStachePathPrism prism = findPrisms(element, JStachePathPrism::getInstanceOn).findFirst().orElse(null);
		if (prism == null) {
			return new PathConfig("", "");
		}
		return new PathConfig(prism.prefix(), prism.suffix());
	}

	private InterfacesConfig resolveBaseInterfaces(TypeElement element) throws AnnotatedException {

		List<JStacheInterfacesPrism> prisms = findPrisms(element, JStacheInterfacesPrism::getInstanceOn).toList();

		List<String> templateInterfaces = prisms.stream().map(JStacheInterfacesPrism::templateImplements)
				.flatMap(faces -> faces.stream()).map(tm -> getTypeName(tm, element)).toList();

		List<String> templateAnnotions = prisms.stream().map(JStacheInterfacesPrism::templateAnnotations)
				.flatMap(faces -> faces.stream()).map(tm -> getTypeName(tm)).toList();

		TypeElement extendsElement = prisms.stream().map(JStacheInterfacesPrism::templateExtends)
				.map(tm -> toTypeElement(tm)).findFirst().orElse(null);

		var modelInterfaces = prisms.stream().map(JStacheInterfacesPrism::modelImplements)
				.flatMap(faces -> faces.stream()).toList();

		for (TypeMirror mi : modelInterfaces) {
			if (!JavaLanguageModel.getInstance().isSubtype(element.asType(), mi)) {
				throw new AnnotatedException(element, "per package declaration of @" + JSTACHE_INTERFACES_CLASS
						+ " model required to implement " + mi.toString());
			}
		}

		return new InterfacesConfig(templateInterfaces, templateAnnotions, extendsElement);
	}

	private <T> Stream<T> findPrisms(TypeElement element, Function<Element, @Nullable T> prismSupplier) {
		return findPrisms(enclosing(element), prismSupplier);
	}

	private <T> Stream<T> findPrisms(Stream<Element> elements, Function<Element, @Nullable T> prismSupplier) {
		return elements.filter(e -> e != null).map(prismSupplier).filter(e -> e != null);
	}

	private static Stream<Element> enclosing(Element e) {
		AbstractSpliterator<Element> split = new AbstractSpliterator<Element>(Long.MAX_VALUE, 0) {
			@Nullable
			Element current = e;

			@Override
			public boolean tryAdvance(Consumer<? super Element> action) {
				if (current == null) {
					return false;
				}
				var c = current;
				current = current.getEnclosingElement();
				action.accept(c);
				return true;
			}
		};
		return StreamSupport.stream(split, false);
	}

	record InterfacesConfig(//
			List<String> templateInterfaces, //
			List<String> templateAnnotations, //
			@Nullable TypeElement extendsElement) {
	}

	private Map<String, NamedTemplate> resolvePartials(TypeElement element) {

		Map<String, NamedTemplate> paths = new LinkedHashMap<>();
		var prisms = findPrisms(element, JStachePartialsPrism::getInstanceOn).toList();
		for (var prism : prisms) {
			var tps = prism.value();
			for (JStachePartialPrism tp : tps) {
				NamedTemplate nt;
				String path = tp.path();
				String name = tp.name();
				assert name != null;
				String template = tp.template();
				nt = resolveNamedTemplate(name, path, template);
				paths.putIfAbsent(name, nt);
			}
		}
		return paths;
	}

	private static NamedTemplate resolveNamedTemplate(String name, @Nullable String path, @Nullable String template) {
		NamedTemplate nt;
		assert name != null;
		if (path != null && !path.isBlank()) {
			nt = new NamedTemplate.FileTemplate(name, path);
		}
		else if (template != null && !template.isEmpty()) {
			nt = new NamedTemplate.InlineTemplate(name, template);
		}
		else {
			nt = new NamedTemplate.FileTemplate(name, name);

		}
		return nt;
	}

	static Map<String, Flag> processorOptionNames;
	static {
		Map<String, Flag> m = new LinkedHashMap<>();
		for (var f : Flag.values()) {
			String name1 = "jstache." + f.name().toLowerCase();
			String name2 = "jstache." + f.name();
			m.put(name1, f);
			m.put(name2, f);
		}
		processorOptionNames = Map.copyOf(m);
	}

	private Set<Flag> resolveFlags(TypeElement element, Map<String, String> options) {
		var prism = findPrisms(element, JStacheFlagsPrism::getInstanceOn) //
				.findFirst().orElse(null);
		var flags = EnumSet.noneOf(Flag.class);

		if (prism != null) {
			prism.flags().stream().map(Flag::valueOf).forEach(flags::add);
		}
		for (var e : options.entrySet()) {
			@Nullable
			Flag flag = processorOptionNames.get(e.getKey());
			if (flag == null) {
				continue;
			}
			if (Boolean.parseBoolean(e.getValue())) {
				flags.add(flag);
			}
			else {
				flags.remove(flag);
			}
		}
		return Collections.unmodifiableSet(flags);
	}

	TypeElement toTypeElement(TypeMirror tm) {
		var e = ((DeclaredType) tm).asElement();
		return (TypeElement) e;
	}

	private String getTypeName(TypeMirror tm) {
		var te = toTypeElement(tm);
		return te.getQualifiedName().toString();
	}

	private String getTypeName(TypeMirror tm, TypeElement modelElement) {
		var dt = (DeclaredType) tm;
		var e = dt.asElement();
		var te = (TypeElement) e;
		String name = te.getQualifiedName().toString();
		var tas = te.getTypeParameters();
		if (tas.isEmpty()) {
			return name;
		}
		if (tas.size() == 1) {
			/*
			 * TODO validation of type parameter Also nullable annotations might need to
			 * be copied
			 */
			return name + "<" + modelElement.getQualifiedName().toString() + ">";
		}
		return name;
	}

	private FormatterTypes resolveFormatterTypes(TypeElement element) {
		var prisms = findPrisms(element, JStacheFormatterTypesPrism::getInstanceOn).toList();
		List<String> classNames = prisms.stream().flatMap(p -> p.types().stream()).map(tm -> getTypeName(tm)).toList();
		List<String> patterns = prisms.stream().flatMap(p -> p.patterns().stream()).toList();
		if (classNames.isEmpty() && patterns.isEmpty()) {
			return FormatterTypes.acceptOnlyKnownTypes();
		}
		return new FormatterTypes.ConfiguredFormatterTypes(classNames, patterns);
	}

	record RendererModel( //
			FormatCallType formatCallType, //
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
			InterfacesConfig ifaces, //
			Set<Flag> flags, Map<String, String> options) implements ProcessingConfig {

		public NamedTemplate namedTemplate() {
			String name;
			String path = path();
			String template = null;
			if (!path.isBlank()) {
				name = path;
			}
			else if (!template().isEmpty()) {
				name = element.getQualifiedName().toString() + "#template";
				template = template();
			}
			else {
				var pe = JavaLanguageModel.getInstance().getElements().getPackageOf(element);
				String folder = pe.getQualifiedName().toString().replace('.', '/');
				path = folder.isEmpty() ? element.getQualifiedName().toString()
						: folder + "/" + element.getSimpleName();
				name = path;
			}
			return resolveNamedTemplate(name, path, template);

		}

		@Override
		public String resourcesPath() {
			String path = options.get(JSTACHE_RESOURCES_PATH_OPTION);
			if (path != null) {
				return path;
			}
			return ProcessingConfig.super.resourcesPath();
		}

	}

	private RendererModel model(TypeElement element, JStachePrism jstache, Map<String, String> options)
			throws DeclarationException, AnnotatedException, DeclarationException {

		if (!element.getTypeParameters().isEmpty()) {
			throw new DeclarationException(
					"Can't generate renderer for class with type variables: " + element.getQualifiedName());
		}

		JStachePrism gp = jstache;

		if (gp == null) {
			throw new AnnotatedException(element, "Missing annotation. bug.");
		}

		FormatCallType formatCallType = resolveFormatCallType(element);

		Charset charset = resolveCharset(element);

		TypeElement contentTypeElement = resolveContentType(element, gp);
		TypeElement formatterElement = resolveFormatter(element, gp);
		String path = gp.path();
		PathConfig pathConfig = resolvePathConfig(element);
		String template = gp.template();
		assert template != null;
		InterfacesConfig ifaces = resolveBaseInterfaces(element);
		ClassRef rendererClassRef = resolveRendererClassRef(element, gp);
		FormatterTypes formatterTypes = resolveFormatterTypes(element);
		Map<String, NamedTemplate> partials = resolvePartials(element);
		Set<Flag> flags = resolveFlags(element, options);

		var model = new RendererModel( //
				formatCallType, //
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
				flags, //
				options);
		return model;
	}

	private FormatCallType resolveFormatCallType(TypeElement element) {
		JStacheType type = findPrisms(element, JStacheConfigPrism::getInstanceOn) //
				.map(config -> JStacheType.valueOf(config.type())).filter(t -> !JStacheType.UNSPECIFIED.equals(t))
				.findFirst().orElse(JStacheType.UNSPECIFIED);

		FormatCallType formatCallType = switch (type) {
			case UNSPECIFIED -> FormatCallType.JSTACHIO;
			case STACHE -> FormatCallType.STACHE;
			case JSTACHIO -> FormatCallType.JSTACHIO;
		};
		return formatCallType;
	}

	private Charset resolveCharset(TypeElement element) {
		@Nullable
		String cs = findPrisms(element, JStacheConfigPrism::getInstanceOn) //
				.map(config -> config.charset()).filter(c -> !c.isBlank()).findFirst().orElse(null);
		cs = cs == null ? "" : cs;
		Charset charset = cs.isBlank() ? StandardCharsets.UTF_8 : Charset.forName(cs);
		return charset;
	}

	private ClassRef resolveRendererClassRef(TypeElement element, JStachePrism gp) {
		String rendererClassSimpleName = resolveAdapterName(element, gp);
		PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(element);
		assert packageElement != null;
		ClassRef rendererClassRef = ClassRef.of(packageElement, rendererClassSimpleName);
		return rendererClassRef;
	}

	private TypeElement resolveContentType(TypeElement element, JStachePrism gp) throws DeclarationException {

		var lm = JavaLanguageModel.getInstance();

		TypeElement autoContentTypeElement = lm.getElements().getTypeElement(UNSPECIFIED_CONTENT_TYPE_CLASS);

		Stream<TypeMirror> contentTypeProviderTypes = //
				findPrisms(element, JStacheConfigPrism::getInstanceOn) //
						.map(p -> p.contentType());

		SneakyFunction<TypeMirror, TypeElement, DeclarationException> f = this::contentTypeElement;

		@Nullable
		TypeElement contentTypeProviderElement = Stream.concat(Stream.of(gp.contentType()), contentTypeProviderTypes) //
				.map(f) //
				.filter(e -> !lm.isSameType(autoContentTypeElement.asType(), e.asType())) //
				.findFirst().orElse(null);

		if (contentTypeProviderElement == null) {
			contentTypeProviderElement = lm.getElements().getTypeElement(HTML_CLASS);
		}

		return contentTypeProviderElement;
	}

	private TypeElement resolveFormatter(TypeElement element, JStachePrism gp) throws DeclarationException {

		var lm = JavaLanguageModel.getInstance();

		TypeElement autoFormatElement = lm.getElements().getTypeElement(UNSPECIFIED_FORMATTER_CLASS);

		Stream<TypeMirror> formatterProviderTypes = //
				findPrisms(element, JStacheConfigPrism::getInstanceOn) //
						.map(p -> p.formatter());

		SneakyFunction<TypeMirror, TypeElement, DeclarationException> f = this::formatterElement;

		@Nullable
		TypeElement formatterProviderElement = formatterProviderTypes //
				.map(f) //
				.filter(e -> !lm.isSameType(autoFormatElement.asType(), e.asType())) //
				.findFirst().orElse(null);

		if (formatterProviderElement == null) {
			formatterProviderElement = lm.getElements().getTypeElement(DEFAULT_FORMATTER_CLASS);
		}

		return formatterProviderElement;
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

	private TypeElement contentTypeElement(TypeMirror templateContentType) throws DeclarationException {
		TypeElement contentTypeElement = null;
		if (templateContentType instanceof DeclaredType dt) {
			contentTypeElement = (TypeElement) dt.asElement();
		}
		else {
			throw new ClassCastException("Expecting DeclaredType for content type " + templateContentType);
		}

		JStacheContentTypePrism formatterPrism = JStacheContentTypePrism.getInstanceOn(contentTypeElement);
		if (formatterPrism == null) {
			throw new DeclarationException(
					contentTypeElement.getQualifiedName() + " class is used as a contentType, but not marked with "
							+ JSTACHE_CONTENT_TYPE_CLASS + " annotation");
		}
		return contentTypeElement;
	}

	private String resolveAdapterName(TypeElement element, JStachePrism gp) {
		String directiveAdapterName = gp.name();
		String adapterClassSimpleName;
		if (directiveAdapterName.isBlank()) {
			JStacheName name = findPrisms(element, JStacheConfigPrism::getInstanceOn) //
					.flatMap(config -> config.naming().stream()).findFirst().orElse(null);

			String prefix = name == null ? JSTACHE_NAME_UNSPECIFIED : name.prefix();

			String suffix = name == null ? JSTACHE_NAME_UNSPECIFIED : name.suffix();

			prefix = prefix.equals(JSTACHE_NAME_UNSPECIFIED) ? JSTACHE_NAME_DEFAULT_PREFIX : prefix;
			suffix = suffix.equals(JSTACHE_NAME_UNSPECIFIED) ? JSTACHE_NAME_DEFAULT_SUFFIX : suffix;

			ClassRef ref = ClassRef.of(element);
			adapterClassSimpleName = ref.getSimpleName() + suffix;
		}
		else {
			adapterClassSimpleName = directiveAdapterName;
		}
		return adapterClassSimpleName;
	}

	private @Nullable ClassRef writeRenderableAdapterClass(TypeElement element, JStachePrism jstache,
			Map<String, String> options) throws AnnotatedException {

		try {
			var model = model(element, jstache, options);
			ProcessingConfig config = model;
			StringWriter stringWriter = new StringWriter();
			try (SwitchablePrintWriter switchablePrintWriter = SwitchablePrintWriter.createInstance(stringWriter)) {
				TextFileObject templateResource = new TextFileObject(config, Objects.requireNonNull(processingEnv));
				JavaLanguageModel javaModel = JavaLanguageModel.getInstance();
				RenderingCodeGenerator codeGenerator = RenderingCodeGenerator.createInstance(javaModel,
						model.formatterTypes(), model.formatCallType());
				CodeWriter codeWriter = new CodeWriter(switchablePrintWriter, codeGenerator, model.partials(), config);
				TemplateClassWriter writer = new TemplateClassWriter(codeWriter, templateResource,
						model.formatCallType());

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
