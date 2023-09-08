
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
import java.io.Writer;
import java.lang.annotation.Inherited;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.AnnotatedException;
import io.jstach.apt.internal.CodeAppendable.StringCodeAppendable;
import io.jstach.apt.internal.FormatterTypes;
import io.jstach.apt.internal.FormatterTypes.FormatCallType;
import io.jstach.apt.internal.LoggingSupport;
import io.jstach.apt.internal.LoggingSupport.MessagerLogging;
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
import io.jstach.apt.prism.JStacheCatalogPrism;
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
import io.jstach.svc.ServiceProvider;

/**
 * Renderer processor
 *
 * @author agentgt
 *
 */
@ServiceProvider(value = Processor.class)
@SupportedOptions({ Prisms.JSTACHE_RESOURCES_PATH_OPTION, //
		Prisms.JSTACHE_INCREMENTAL_OPTION, //
		Prisms.JSTACHE_FLAGS_DEBUG, //
		Prisms.JSTACHE_FLAGS_NO_INVERTED_BROKEN_CHAIN, //
		Prisms.JSTACHE_FLAGS_NO_NULL_CHECKING, GenerateRendererProcessor.JSTACHE_CLAIM_ANNOTATIONS, //
		GenerateRendererProcessor.JSTACHE_GRADLE_INCREMENTAL //
})
public class GenerateRendererProcessor extends AbstractProcessor implements Prisms {

	/**
	 * No-arg constructor for service loader.
	 */
	public GenerateRendererProcessor() {
	}

	/*
	 * TODO doc or remove for for minor release. This was for gradle bug fix: #223
	 */
	static final String JSTACHE_GRADLE_INCREMENTAL = "jstache.gradle_incremental";

	static final String JSTACHE_CLAIM_ANNOTATIONS = "jstache.claim_annotations";

	private enum GradleIncremental {

		isolating, aggregating, disable;

	}

	private GradleIncremental gradleIncremental = GradleIncremental.disable;

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	Set<JStacheRef> rendererClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());

	Set<CatalogRef> catalogClasses = Collections.newSetFromMap(new ConcurrentHashMap<>());

	private final List<ElementMessage> errors = new ArrayList<>();

	boolean globalDebug = false;

	boolean catalogGenerated = false;

	boolean generateServiceFiles = true;

	boolean claimAnnotations = false;

	private static String formatErrorMessage(Position position, @Nullable String message) {
		message = message == null ? "" : message;
		String formatString = "%s:%d: error: %s%n%s%n%s%nsymbol: mustache directive%nlocation: mustache template";
		@Nullable
		Object @NonNull [] fields = new @Nullable Object @NonNull [] { position.fileName(), //
				position.row(), //
				message, //
				// Tabs make for confusing column reporting
				position.currentLine().replace('\t', ' '), //
				columnPositioningString(position.col()) };
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
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		var opts = processingEnv.getOptions();

		this.claimAnnotations = Boolean.parseBoolean(opts.get(JSTACHE_CLAIM_ANNOTATIONS));

		boolean incremental = Boolean.parseBoolean(opts.get(JSTACHE_INCREMENTAL_OPTION));

		GradleIncremental gradleIncremental = Optional.ofNullable(opts.get(JSTACHE_GRADLE_INCREMENTAL))
				.map(GradleIncremental::valueOf).orElse(null);

		if (gradleIncremental == null) {
			gradleIncremental = incremental ? GradleIncremental.isolating : GradleIncremental.disable;
		}
		this.gradleIncremental = gradleIncremental;

		globalDebug = resolveFlags(opts, null).contains(Flag.DEBUG);
		LoggingSupport.RootLogging rootLogging = new LoggingSupport.RootLogging(processingEnv.getMessager(),
				globalDebug);
		if (globalDebug) {
			for (var e : opts.entrySet()) {
				var k = e.getKey();
				var v = e.getValue();
				if (k.startsWith("jstache.")) {
					rootLogging.debug(k + " = " + v);
				}
			}
		}
		if (gradleIncremental == GradleIncremental.isolating) {
			generateServiceFiles = false;
			rootLogging
					.info("Incremental is turned on so catalogs and service provider files will not be (re)generated!");
		}
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Set.copyOf(Prisms.ANNOTATIONS);
	}

	@Override
	public Set<String> getSupportedOptions() {
		if (this.gradleIncremental != GradleIncremental.disable) {
			String gradleFlag = this.gradleIncremental.name();
			gradleFlag = "org.gradle.annotation.processing." + gradleFlag;
			return Stream.concat(Stream.of(gradleFlag), super.getSupportedOptions().stream())
					.collect(Collectors.toSet());
		}

		return super.getSupportedOptions();
	}

	@SuppressWarnings("DoNotClaimAnnotations") // it is configurable just in case
	@Override
	public boolean process(Set<? extends TypeElement> processAnnotations, RoundEnvironment roundEnv) {
		try {
			_process(roundEnv);
			return claimAnnotations;
		}
		catch (AnnotatedException e) {
			e.report(processingEnv.getMessager());
			return claimAnnotations;
		}
	}

	private void _process(RoundEnvironment roundEnv) throws AnnotatedException {
		/*
		 * Lets just bind the damn utils so that we do not have to pass them around
		 * everywhere
		 */
		JavaLanguageModel.createInstance(processingEnv.getTypeUtils(), processingEnv.getElementUtils(),
				processingEnv.getMessager());
		Map<String, String> options = processingEnv.getOptions();

		LoggingSupport.RootLogging rootLogging = new LoggingSupport.RootLogging(processingEnv.getMessager(),
				globalDebug);

		if (roundEnv.processingOver()) {
			for (ElementMessage error : errors) {
				TypeElement element = processingEnv.getElementUtils().getTypeElement(error.qualifiedElementName());
				processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, error.message(), element);
			}
			ClassRef serviceClass = ClassRef.ofBinaryName(TEMPLATE_PROVIDER_CLASS);
			Stream<ClassRef> renderers = rendererClasses.stream().filter(jr -> jr.jstachio() && jr.pub())
					.map(jr -> jr.classRef());
			Stream<ClassRef> catalogs = catalogClasses.stream()
					.filter(c -> c.flags().contains(CatalogFlag.GENERATE_PROVIDER_META_INF_SERVICE))
					.map(c -> c.classRef());

			if (generateServiceFiles) {
				ServicesFiles.writeServicesFile(processingEnv.getFiler(), rootLogging, serviceClass,
						Stream.concat(catalogs, renderers).toList());
				ClassRef extensionClass = ClassRef.ofBinaryName(JSTACHIO_EXTENSION_CLASS);

				var finders = catalogClasses.stream()
						.filter(c -> c.flags().contains(CatalogFlag.GENERATE_FINDER_META_INF_SERVICE))
						.map(c -> c.classRef()).toList();

				ServicesFiles.writeServicesFile(processingEnv.getFiler(), rootLogging, extensionClass, finders);
			}

			if (!catalogGenerated) {
				/*
				 * We try to avoid generate the catalog on the last round however it can
				 * happen.
				 */
				generateCatalog();
			}
			return;
		}
		else {
			boolean found = false;
			TypeElement jstacheCatalogElement = processingEnv.getElementUtils().getTypeElement(JSTACHE_CATALOG_CLASS);
			for (Element element : roundEnv.getElementsAnnotatedWith(jstacheCatalogElement)) {
				PackageElement packageElement = (PackageElement) element;
				JStacheCatalogPrism jstacheCatalog = JStacheCatalogPrism.getInstanceOn(packageElement);
				if (jstacheCatalog == null) {
					throw new IllegalStateException("This might be a bug. JStacheCatalogPrism failed.");
				}
				String catalogName = jstacheCatalog.name();
				var classRef = ClassRef.of(packageElement, catalogName);
				catalogClasses.add(new CatalogRef(jstacheCatalog, classRef, packageElement));
				found = true;
			}

			TypeElement jstacheElement = processingEnv.getElementUtils().getTypeElement(JSTACHE_CLASS);
			for (Element element : roundEnv.getElementsAnnotatedWith(jstacheElement)) {
				TypeElement classElement = (TypeElement) element;
				JStachePrism jstache = JStachePrism.getInstanceOn(classElement);
				if (jstache == null) {
					throw new IllegalStateException("This might be a bug. JStachePrism failed.");
				}
				@Nullable
				JStacheRef ref = writeRenderableAdapterClass(classElement, jstache, options);
				if (ref != null) {
					rendererClasses.add(ref);
				}
				found = true;
			}

			if (!found) {
				/*
				 * Nothing was found on this round so we generate the catalog
				 */
				generateCatalog();
			}

			return;
		}
	}

	private void generateCatalog() {
		if (catalogGenerated || !generateServiceFiles)
			return;
		catalogGenerated = true;
		for (var cat : catalogClasses) {
			var cc = cat.classRef();
			CatalogClassWriter cw = new CatalogClassWriter(cc.getPackageName(), cc.getSimpleName());
			cw.addAll(rendererClasses.stream()
					.filter(js -> js.jstachio() && (js.pub() || js.classRef().isSamePackage(cc)))
					.map(js -> js.classRef()).toList());
			cw.write(processingEnv.getFiler(), cat.logging());
		}
	}

	@Nullable
	JStachePathPrism pathInstanceOn(Element element) {
		var prism = JStachePathPrism.getInstanceOn(element);
		if (prism != null) {
			return prism;
		}
		var config = JStacheConfigPrism.getInstanceOn(element);
		if (config == null || config.pathing() == null || config.pathing().isEmpty()) {
			return null;
		}
		return config.pathing().get(0);
	}

	private PathConfig resolvePathConfig(TypeElement element) {
		JStachePathPrism prism = findPrisms(element, this::pathInstanceOn).findFirst().orElse(null);
		final String defaultPrefix = Prisms.JSTACHE_PATH_DEFAULT_PREFIX;
		final String defaultSuffix = Prisms.JSTACHE_PATH_DEFAULT_SUFFIX;
		if (prism == null) {
			return new PathConfig(defaultPrefix, defaultSuffix, true, true);
		}
		String prefix = prism.prefix();
		String suffix = prism.suffix();
		boolean prefixUnspecified = Prisms.JSTACHE_PATH_UNSPECIFIED.equals(prefix);
		boolean suffixUnspecified = Prisms.JSTACHE_PATH_UNSPECIFIED.equals(suffix);

		prefix = prefixUnspecified ? defaultPrefix : prefix;
		suffix = suffixUnspecified ? defaultSuffix : suffix;
		return new PathConfig(prefix, suffix, prefixUnspecified, suffixUnspecified);
	}

	@Nullable
	JStacheInterfacesPrism interfacesInstanceOn(Element element) {
		var prism = JStacheInterfacesPrism.getInstanceOn(element);
		if (prism != null) {
			return prism;
		}
		var config = JStacheConfigPrism.getInstanceOn(element);
		if (config == null || config.interfacing() == null || config.interfacing().isEmpty()) {
			return null;
		}
		return config.interfacing().get(0);
	}

	private InterfacesConfig resolveBaseInterfaces(TypeElement element) throws AnnotatedException {

		List<JStacheInterfacesPrism> prisms = findPrisms(element, this::interfacesInstanceOn).toList();

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

	private <T> Stream<@NonNull T> findPrisms(TypeElement element, Function<Element, @Nullable T> prismSupplier) {
		return this.<@NonNull T>findPrisms(expandUsing(enclosing(element)), prismSupplier);
	}

	private <T> Stream<T> findPrisms(Stream<? extends Element> elements, Function<Element, @Nullable T> prismSupplier) {
		return elements.filter(e -> e != null).map(prismSupplier).flatMap(p -> Stream.ofNullable(p));
	}

	private static Stream<Element> enclosing(Element e) {
		AbstractSpliterator<Element> split = new AbstractSpliterator<Element>(Long.MAX_VALUE, Spliterator.ORDERED) {
			@Nullable
			Element current = e;

			@Override
			public boolean tryAdvance(Consumer<? super Element> action) {
				var c = current;
				if (c == null) {
					return false;
				}
				current = c.getEnclosingElement();
				action.accept(c);
				return true;
			}
		};
		return StreamSupport.stream(split, false);
	}

	private Stream<Element> expandUsing(Stream<Element> e) {
		return e.flatMap(this::expandUsing);
	}

	/*
	 * This is to get referenced config of JStacheConfig.using
	 */
	private Stream<Element> expandUsing(Element e) {

		JStacheConfigPrism config = JStacheConfigPrism.getInstanceOn(e);
		if (config == null) {
			return Stream.of(e);
		}
		var using = config.using();
		if (!using.toString().equals(void.class.getCanonicalName())) {
			if (using instanceof DeclaredType dt && dt.asElement() instanceof TypeElement te) {
				return Stream.of(e, te);
			}
		}
		return Stream.of(e);
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
				nt = resolveNamedTemplate(name, path, template, element, prism.mirror);
				paths.putIfAbsent(name, nt);
			}
		}
		return paths;
	}

	private static NamedTemplate resolveNamedTemplate(String name, @Nullable String path, @Nullable String template,
			TypeElement element, AnnotationMirror annotationMirror) {
		NamedTemplate nt;
		assert name != null;
		if (path != null && !path.isBlank()) {
			nt = new NamedTemplate.FileTemplate(name, path, element, annotationMirror);
		}
		else if (template != null && !template.isEmpty()) {
			nt = new NamedTemplate.InlineTemplate(name, template, element, annotationMirror);
		}
		else {
			nt = new NamedTemplate.FileTemplate(name, name, element, annotationMirror);

		}
		return nt;
	}

	static Map<String, Flag> processorOptionNames;
	static {
		Map<String, Flag> m = new LinkedHashMap<>();
		for (var f : Flag.values()) {
			String name1 = "jstache." + f.name().toLowerCase(Locale.ENGLISH);
			String name2 = "jstache." + f.name();
			m.put(name1, f);
			m.put(name2, f);
		}
		processorOptionNames = Map.copyOf(m);
	}

	private Set<Flag> resolveFlags(Map<String, String> options, @Nullable TypeElement element) {
		var flags = EnumSet.noneOf(Flag.class);
		Stream.ofNullable(element) //
				.<JStacheFlagsPrism>flatMap(e -> findPrisms(e, JStacheFlagsPrism::getInstanceOn)) //
				.filter(p -> !this.isFlagsUnspecified(p)) //
				.limit(1) //
				.flatMap(p -> p.flags().stream()) //
				.map(Flag::valueOf) //
				.forEach(flags::add);
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

	boolean isFlagsUnspecified(JStacheFlagsPrism prism) {
		for (String f : prism.flags()) {
			if (Flag.UNSPECIFIED.name().equals(f)) {
				return true;
			}
		}
		return false;
	}

	private String resolveNullableAnnotation(TypeElement element) {
		String defaultNullableName = Inherited.class.getName();
		@Nullable
		String annotation = findPrisms(element, JStacheFlagsPrism::getInstanceOn)
				.map(p -> p.nullableAnnotation().toString()).filter(tm -> !tm.equals(defaultNullableName)).findFirst()
				.orElse(null);

		if (annotation == null) {
			annotation = "/* @Nullable */";
		}
		else {
			annotation = "@" + annotation;
		}
		return annotation;
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

	private FormatterTypes resolveFormatterTypes(TypeElement element, @Nullable TypeElement formatterElement) {
		/*
		 * First get the formatter types based on config
		 */
		var prisms = findPrisms(element, JStacheFormatterTypesPrism::getInstanceOn).toList();
		List<String> classNames = prisms.stream().flatMap(p -> p.types().stream()).map(tm -> getTypeName(tm))
				.collect(Collectors.toCollection(ArrayList::new));
		List<String> patterns = prisms.stream().flatMap(p -> p.patterns().stream())
				.collect(Collectors.toCollection(ArrayList::new));

		/*
		 * Now we get the formatter types off the selected JStacheFormatter and whatever
		 * classes it inherits from
		 */

		Stream<TypeElement> formatterSupers;

		if (formatterElement == null) {
			formatterSupers = Stream.empty();
		}
		else {
			formatterSupers = JavaLanguageModel.getInstance().supers(formatterElement);
		}

		var formatterTypesOnFormatter = findPrisms(formatterSupers, JStacheFormatterTypesPrism::getInstanceOn).toList();

		classNames.addAll(formatterTypesOnFormatter.stream() //
				.flatMap(p -> p.types().stream()) //
				.map(tm -> getTypeName(tm)).toList());

		patterns.addAll(formatterTypesOnFormatter.stream().flatMap(p -> p.patterns().stream()).toList());

		if (classNames.isEmpty() && patterns.isEmpty()) {
			return FormatterTypes.acceptOnlyKnownTypes();
		}
		return new FormatterTypes.ConfiguredFormatterTypes(classNames, patterns);
	}

	record RendererModel( //
			FormatCallType formatCallType, //
			TypeElement element, //
			AnnotationMirror annotationMirror, //
			ClassRef rendererClassRef, //
			String path, //
			PathConfig pathConfig, //
			String template, //
			Charset charset, //
			Optional<TypeElement> contentTypeElement, //
			FormatterTypes formatterTypes, //
			Optional<TypeElement> formatterTypeElement, //
			Map<String, NamedTemplate> partials, //
			InterfacesConfig ifaces, //
			String nullableAnnotation, Set<Flag> flags, Map<String, String> options) implements ProcessingConfig {

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
				String folder = pe == null ? "" : pe.getQualifiedName().toString().replace('.', '/');
				path = folder.isEmpty() ? element.getQualifiedName().toString()
						: folder + "/" + element.getSimpleName();
				if (pathConfig.suffixUnspecified()) {
					path += Prisms.JSTACHE_PATH_AUTO_SUFFIX;
				}
				name = path;
			}
			return resolveNamedTemplate(name, path, template, element, annotationMirror);

		}

		@Override
		public List<String> resourcesPaths() {
			String path = options.get(JSTACHE_RESOURCES_PATH_OPTION);
			if (path != null) {
				return Stream.of(path.split(",")).filter(p -> !p.isBlank()).toList();
			}
			return List.of();
		}

		@Override
		public boolean fallbackToFilesystem() {
			String path = options.get(JSTACHE_RESOURCES_PATH_OPTION);
			if (path != null && path.isBlank()) {
				return false;
			}
			return true;
		}

		@Override
		public @Nullable AnnotationMirror annotationToLog() {
			return null;
		}

		@Override
		public TypeElement elementToLog() {
			return element;
		}

		@Override
		public Messager messager() {
			return JavaLanguageModel.getInstance().getMessager();
		}

		// The below bullshit is because eclipse is broken for
		// records that implement an interface with null constraints
		// even if the attributes are nonnull.
		//
		public Map<String, NamedTemplate> partials() {
			return this.partials;
		}

		@Override
		public Set<Flag> flags() {
			return this.flags;
		}

		@Override
		public Charset charset() {
			return this.charset;
		}

		@Override
		public PathConfig pathConfig() {
			return this.pathConfig;
		}

	}

	private RendererModel model(TypeElement element, JStachePrism jstache, Map<String, String> options)
			throws DeclarationException, AnnotatedException, DeclarationException {

		if (!element.getTypeParameters().isEmpty()) {
			throw new DeclarationException(
					"Can't generate renderer for class with type variables: " + element.getQualifiedName());
		}

		JStachePrism gp = jstache;

		FormatCallType formatCallType = resolveFormatCallType(element);

		Charset charset = resolveCharset(element);

		@Nullable
		TypeElement contentTypeElement = resolveContentType(element);
		if (contentTypeElement == null && formatCallType != FormatCallType.STACHE) {
			throw new AnnotatedException(element,
					"Content Type provider class is missing which usually is a classpath issue"
							+ " or the JStache type was supposed to be zero dep (JStacheType.STACHE)");
		}

		@Nullable
		TypeElement formatterElement = resolveFormatter(element);
		if (formatterElement == null && formatCallType != FormatCallType.STACHE) {
			throw new AnnotatedException(element,
					"Formatter provider class is missing which usually is a classpath issue"
							+ " or the JStache type was supposed to be zero dep (JStacheType.STACHE)");
		}

		AnnotationMirror annotationMirror = gp.mirror;
		String path = gp.path();
		PathConfig pathConfig = resolvePathConfig(element);
		String template = gp.template();
		assert template != null;
		InterfacesConfig ifaces = resolveBaseInterfaces(element);
		ClassRef rendererClassRef = resolveRendererClassRef(element, gp);
		FormatterTypes formatterTypes = resolveFormatterTypes(element, formatterElement);
		Map<String, NamedTemplate> partials = resolvePartials(element);
		Set<Flag> flags = resolveFlags(options, element);
		String nullableAnnotation = resolveNullableAnnotation(element);
		var model = new RendererModel( //
				formatCallType, //
				element, //
				annotationMirror, //
				rendererClassRef, //
				path, //
				pathConfig, //
				template, //
				charset, //
				Optional.ofNullable(contentTypeElement), //
				formatterTypes, //
				Optional.ofNullable(formatterElement), //
				partials, //
				ifaces, //
				nullableAnnotation, flags, //
				options);
		return model;
	}

	private FormatCallType resolveFormatCallType(TypeElement element) {
		JStacheType type = findPrisms(element, JStacheConfigPrism::getInstanceOn) //
				.map(config -> JStacheType.valueOf(config.type())) //
				.filter(t -> !JStacheType.UNSPECIFIED.equals(t)) //
				.findFirst() //
				.orElseGet(() -> JStacheType.UNSPECIFIED);

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

	private @Nullable TypeElement resolveContentType(TypeElement element) throws DeclarationException {

		var lm = JavaLanguageModel.getInstance();

		TypeElement autoContentTypeElement = lm.getElements().getTypeElement(UNSPECIFIED_CONTENT_TYPE_CLASS);

		Stream<TypeMirror> contentTypeProviderTypes = //
				findPrisms(element, JStacheConfigPrism::getInstanceOn) //
						.map(p -> p.contentType());

		SneakyFunction<TypeMirror, TypeElement, DeclarationException> f = this::contentTypeElement;

		@Nullable
		TypeElement contentTypeProviderElement = contentTypeProviderTypes //
				.map(f) //
				.filter(e -> !lm.isSameType(autoContentTypeElement.asType(), e.asType())) //
				.findFirst().orElse(null);

		if (contentTypeProviderElement == null) {
			contentTypeProviderElement = lm.getElements().getTypeElement(HTML_CLASS);
		}

		return contentTypeProviderElement;
	}

	private @Nullable TypeElement resolveFormatter(TypeElement element) throws DeclarationException {

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
			adapterClassSimpleName = prefix + ref.getSimpleName() + suffix;
		}
		else {
			adapterClassSimpleName = directiveAdapterName;
		}
		return adapterClassSimpleName;
	}

	private @Nullable JStacheRef writeRenderableAdapterClass(TypeElement element, JStachePrism jstache,
			Map<String, String> options) throws AnnotatedException {

		@Nullable
		ProcessingConfig config = null;
		try {
			var model = model(element, jstache, options);
			config = model;
			StringBuilder stringWriter = new StringBuilder();
			StringCodeAppendable codeAppendable = new StringCodeAppendable(stringWriter);

			TextFileObject templateResource = new TextFileObject(config, Objects.requireNonNull(processingEnv));
			JavaLanguageModel javaModel = JavaLanguageModel.getInstance();
			RenderingCodeGenerator codeGenerator = RenderingCodeGenerator.createInstance(javaModel,
					model.formatterTypes(), model.formatCallType());
			CodeWriter codeWriter = new CodeWriter(codeAppendable, codeGenerator, model.partials(), config);
			TemplateClassWriter writer = new TemplateClassWriter(codeWriter, templateResource, model.formatCallType());

			writer.writeRenderableAdapterClass(model);

			JavaFileObject sourceFile = processingEnv.getFiler()
					.createSourceFile(model.rendererClassRef().requireCanonicalName(), element);
			/*
			 * Should we use the templates charset? Probably safest to use UTF-8.
			 */
			try (OutputStream stream = sourceFile.openOutputStream();
					Writer outputWriter = new OutputStreamWriter(stream, StandardCharsets.UTF_8);) {
				outputWriter.append(stringWriter.toString());
			}

			boolean pub = element.getModifiers().contains(Modifier.PUBLIC);
			boolean jstachio = switch (model.formatCallType()) {
				case JSTACHIO -> true;
				case STACHE -> false;
				case JSTACHIO_BYTE -> throw new IllegalStateException();
			};

			return new JStacheRef(model.rendererClassRef(), pub, jstachio);
		}
		catch (ProcessingException ex) {
			if (config != null) {
				config.debug(ex);
			}
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

	record JStacheRef(ClassRef classRef, boolean pub, boolean jstachio) {
	}

	record CatalogRef(JStacheCatalogPrism prism, ClassRef classRef, Element element) {

		EnumSet<CatalogFlag> flags() {
			var flags = EnumSet.noneOf(CatalogFlag.class);
			flags.addAll(prism.flags().stream().map(f -> CatalogFlag.valueOf(f)).toList());
			return flags;
		}

		MessagerLogging logging() {
			return new LoggingSupport.AdHocMessager("[JSTACHIO CATALOG] ", false, element, prism.mirror);
		}

	}

}
