package io.jstach.jstachio.spi;

import static java.util.Objects.requireNonNull;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheContentType;
import io.jstach.jstache.JStacheContentType.UnspecifiedContentType;
import io.jstach.jstache.JStacheFormatter;
import io.jstach.jstache.JStacheFormatter.UnspecifiedFormatter;
import io.jstach.jstache.JStacheName;
import io.jstach.jstache.JStachePath;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Output.EncodedOutput;
import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateConfig;
import io.jstach.jstachio.TemplateInfo;
import io.jstach.jstachio.escapers.Html;
import io.jstach.jstachio.escapers.PlainText;
import io.jstach.jstachio.formatters.DefaultFormatter;
import io.jstach.jstachio.formatters.SpecFormatter;
import io.jstach.jstachio.spi.Templates.TemplateInfos.SimpleTemplateInfo;

/**
 *
 * Locates generated templates by their model via reflection.
 * <p>
 * This utility class is useful if you plan on implementing your own {@link JStachio} and
 * or other integrations.
 *
 * @apiNote In order to use reflection in a modular setup one must <code>open</code>
 * packages to the {@link io.jstach.jstachio/ } module.
 * @author agentgt
 *
 */
public final class Templates {

	private Templates() {
	}

	/**
	 * A utility method that will check if the templates encoding matches the outputs
	 * encoding.
	 * @param template template charset to check
	 * @param output an encoded output expecting the template charset to be the same.
	 * @throws UnsupportedCharsetException if the charsets do not match
	 */
	public static void validateEncoding(TemplateInfo template, EncodedOutput<?> output) {
		if (!template.templateCharset().equals(output.charset())) {
			throw new UnsupportedCharsetException(
					"The encoding of the template does not match the output. template charset="
							+ template.templateCharset() + ", output charset=" + output.charset());
		}
	}

	/**
	 * Finds a {@link Template} if possible otherwise falling back to a
	 * {@link TemplateInfo} based on annotation metadata. A call first resolves the type
	 * that is actually annotated with JStache and then effectively calls
	 * {@link #getTemplate(Class)} first and if that fails possibly tries
	 * {@link #getInfoByReflection(Class)} based on config.
	 * @apiNote Callers can do an <code>instanceof Template t</code> to see if a generated
	 * template was returned instead of the fallback.
	 * @param modelType the models class (<em>the one annotated with {@link JStache} and
	 * not the Templates class</em>)
	 * @param config config used to determine whether or not to fallback
	 * @return the template info which might be a {@link Template} if the generated
	 * template was found.
	 * @throws Exception if any reflection error happes or the template is not found
	 */
	public static TemplateInfo findTemplate(Class<?> modelType, JStachioConfig config) throws Exception {
		Logger logger = config.getLogger(Templates.class.getName());
		return findTemplate(modelType, config, logger);
	}

	/**
	 * Finds a {@link Template} if possible otherwise falling back to a
	 * {@link TemplateInfo} based on annotation metadata. A call first resolves the type
	 * that is actually annotated with JStache and then effectively calls
	 * {@link #getTemplate(Class)} first and if that fails possibly tries
	 * {@link #getInfoByReflection(Class)} based on config. Unlike
	 * {@link #findTemplate(Class, JStachioConfig)} this call will not produce any logging
	 * and will not throw an exception if it fails.
	 * @apiNote Callers can do an <code>instanceof Template t</code> to see if a generated
	 * template was returned instead of the fallback.
	 * @param modelType the models class (<em>the one annotated with {@link JStache} and
	 * not the Templates class</em>)
	 * @param config config used to determine whether or not to fallback
	 * @return the template info which might be a {@link Template} if the generated
	 * template was found or <code>null</code> if not found.
	 */
	public static @Nullable TemplateInfo findTemplateOrNull(Class<?> modelType, JStachioConfig config) {
		if (isIgnoredType(modelType)) {
			return null;
		}
		try {
			return findTemplate(modelType, config, JStachioConfig.noopLogger());
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 * Checks to see if the model type is a type that is always ignored for faster
	 * {@link JStachioTemplateFinder#supportsType(Class)} checking.
	 *
	 * TODO possible candidate for public on minor release
	 * @param modelType the jstache annotated type
	 * @return <code>true</code> if the type should be ignored.
	 */
	static boolean isIgnoredType(Class<?> modelType) {
		/*
		 * TODO JMH as this method will be called quite frequently
		 */
		if (modelType == String.class || modelType == Map.class || modelType == Object.class
				|| modelType.isPrimitive()) {
			return true;
		}
		if (modelType.getName().startsWith("java.")) {
			return true;
		}
		return false;
	}

	/**
	 * Finds the closest JStache annotation on this class or parent classes (super and
	 * interfaces).
	 *
	 * TODO possible candidate to make public on minor release
	 * @param modelType the model type to search on.
	 * @return a tuple of found class annotated and the jstache annotation.
	 */
	static Entry<Class<?>, JStache> findJStache(final Class<?> modelType) {
		var jstache = findJStacheOrNull(modelType);
		if (jstache == null) {
			throw new TemplateNotFoundException("JStache annotation was not found on type or parents.", modelType);
		}
		return jstache;
	}

	/**
	 * Finds the closest JStache annotation on this class or parent classes (super and
	 * interfaces).
	 *
	 * TODO possible candidate to make public on minor release
	 * @param modelType the model type to search on.
	 * @return a tuple of found class annotated and the jstache annotation.
	 */
	static @Nullable Entry<Class<?>, JStache> findJStacheOrNull(final Class<?> modelType) {
		if (isIgnoredType(modelType))
			return null;
		return Stream.concat(parents(modelType), interfaces(modelType)) //
				.filter(_c -> !isIgnoredType(_c)) //
				.flatMap(_c -> Stream.ofNullable(_c.getDeclaredAnnotation(JStache.class)) //
						.map(a -> Map.<Class<?>, JStache>entry(_c, a))) //
				.findFirst() //
				.orElse(null);
	}

	static TemplateInfo findTemplate(Class<?> modelType, JStachioConfig config, Logger logger) throws Exception {

		var resolvedType = findJStache(modelType).getKey();

		EnumSet<TemplateLoadStrategy> strategies = EnumSet.noneOf(TemplateLoadStrategy.class);

		for (var s : ALL_STRATEGIES) {
			if (s.isEnabled(config)) {
				strategies.add(s);
			}
		}
		var classLoaders = collectClassLoaders(modelType.getClassLoader());

		Exception error;
		try {
			Template<?> r = Templates.getTemplate(resolvedType, strategies, classLoaders, logger);
			return r;
		}
		catch (Exception e) {
			error = e;
		}
		if (!config.getBoolean(JStachioConfig.REFLECTION_TEMPLATE_DISABLE)) {
			if (logger.isLoggable(Level.WARNING)) {
				String message = String
						.format("Could not find generated template and will try reflection for model type:"
								+ "'%s', annotated type: '%s'", modelType, resolvedType);
				logger.log(Level.WARNING, message, error);
			}
			return getInfoByReflection(resolvedType);

		}
		throw error;

	}

	/**
	 * Finds template info by accessing JStache annotations through reflective lookup.
	 * <p>
	 * This allows you to lookup template meta data <strong>regardless of whether or not
	 * the annotation processor has generated code</strong>. This method is mainly used
	 * for fallback mechanisms and extensions.
	 * <p>
	 * Why might you need the reflective data instead of the static generated meta data?
	 * Well often times the annotation processor in a hot reload environment such as
	 * JRebel, JBoss modules, or Spring Reload has not generated the code from a JStache
	 * model and or it is not desired. This allows reflection based engines like JMustache
	 * to keep working even if code is not generated.
	 * @param modelType the class that is annotated with {@link JStache}
	 * @return template info meta data
	 * @throws Exception if any reflection error happes or the template is not found
	 */
	public static TemplateInfo getInfoByReflection(Class<?> modelType) throws Exception {
		return TemplateInfos.templateOf(modelType);
	}

	/**
	 * Finds a template by reflection or an exception is thrown.
	 * @param <T> the model type
	 * @param clazz the model type
	 * @return the template never <code>null</code>.
	 * @throws NoSuchElementException if the template is not found
	 * @throws Exception if the template is not found or any reflective access errors
	 */
	public static <T> Template<T> getTemplate(Class<T> clazz) throws Exception {
		List<ClassLoader> classLoaders = collectClassLoaders(clazz.getClassLoader());
		return getTemplate(clazz, ALL_STRATEGIES, classLoaders, JStachioConfig.noopLogger());
	}

	/**
	 * Finds a template by reflection or an exception is thrown. <em>Because the return
	 * template is parameterized a template matching the exact type is returned and
	 * inheritance either via interfaces or super class is not checked!</em>
	 * @param <T> the model type
	 * @param modelType the model type
	 * @param strategies load strategies
	 * @param classLoaders class loaders to try loading
	 * @param logger used to log attempted strategies. If you do not want to log use
	 * {@link JStachioConfig#noopLogger()}.
	 * @return the template never <code>null</code>.
	 * @throws NoSuchElementException if the template is not found
	 * @throws Exception if the template is not found or any reflective access errors
	 *
	 */
	public static <T> Template<T> getTemplate(Class<T> modelType, Iterable<TemplateLoadStrategy> strategies,
			Iterable<ClassLoader> classLoaders, System.Logger logger) throws Exception {
		for (TemplateLoadStrategy s : strategies) {
			if (logger.isLoggable(Level.DEBUG)) {
				logger.log(Level.DEBUG, "For modelType: \"" + modelType + "\" trying strategy: \"" + s + "\"");
			}
			for (ClassLoader classLoader : classLoaders) {
				try {
					Template<T> template = s.load(modelType, classLoader, logger);
					if (template != null) {
						return template;
					}
				}
				catch (ClassNotFoundException | TemplateNotFoundException e) {
					continue;
				}
			}
		}
		throw new TemplateNotFoundException(modelType);
	}

	static boolean isReflectionTemplate(TemplateInfo template) {
		if (template instanceof SimpleTemplateInfo si) {
			return true;
		}
		return false;
	}

	private static final Set<TemplateLoadStrategy> ALL_STRATEGIES = EnumSet.allOf(TemplateLoadStrategy.class);

	/**
	 * Strategy to load templates dynamically. <em>These strategies expect the exact type
	 * and not a super type!</em>
	 *
	 * @author agentgt
	 *
	 */
	public enum TemplateLoadStrategy {

		/**
		 * Strategy that will try the {@link ServiceLoader} with the SPI of
		 * {@link TemplateProvider}.
		 */
		SERVICE_LOADER() {
			@SuppressWarnings("unchecked")
			@Override
			protected <T> @Nullable Template<T> load(Class<T> clazz, ClassLoader classLoader, System.Logger logger)
					throws Exception {
				return (Template<T>) templateByServiceLoader(clazz, classLoader, logger);
			}

			@Override
			protected final boolean isEnabled(JStachioConfig config) {
				return !config.getBoolean(JStachioConfig.SERVICELOADER_TEMPLATE_DISABLE);
			}
		},
		/**
		 * Strategy that will try no-arg constructor
		 */
		CONSTRUCTOR() {
			@Override
			protected <T> @Nullable Template<T> load(Class<T> clazz, ClassLoader classLoader, System.Logger logger)
					throws Exception {
				return templateByConstructor(clazz, classLoader);
			}

			@Override
			protected final boolean isEnabled(JStachioConfig config) {
				return !config.getBoolean(JStachioConfig.REFLECTION_TEMPLATE_DISABLE);
			}

		};

		/**
		 * Load the template by this strategy.
		 * @param <T> model type.
		 * @param clazz model type.
		 * @param classLoader classload which may more may not be used.
		 * @param logger used to log reflection warnings or other errors.
		 * @return loaded template
		 * @throws Exception if an error happens while trying to load template.
		 */
		protected abstract <T> @Nullable Template<T> load(Class<T> clazz, ClassLoader classLoader, System.Logger logger)
				throws Exception;

		/**
		 * Determine if the strategy is enabled.
		 * @param config key value config
		 * @return true if not disabled.
		 */
		protected abstract boolean isEnabled(JStachioConfig config);

	}

	@SuppressWarnings("unchecked")
	private static <T> @Nullable Template<T> templateByConstructor(Class<T> clazz, ClassLoader classLoader)
			throws Exception {
		Class<?> implementation = classLoader.loadClass(generatedClassName(clazz));
		Constructor<?> constructor = implementation.getDeclaredConstructor();
		constructor.setAccessible(true);
		return (Template<T>) constructor.newInstance();
	}

	/**
	 * Gets the canonical class name of the generated template code regardless of whether
	 * or not code has actually been generated. Because the class may not have been
	 * generated the return is a String.
	 * @param modelClass the exact model class that contains the {@link JStache}
	 * annotation.
	 * @return the FQN class name of the would be generated template code
	 * @throws NoSuchElementException if the model class is not annotated with
	 * {@link JStache}.
	 */
	public static String generatedClassName(Class<?> modelClass) {
		// TODO perhaps this information should be on TemplateInfo?
		var a = modelClass.getAnnotation(JStache.class);
		if (a == null) {
			throw new TemplateNotFoundException(modelClass);
		}
		String cname;
		if (a.name().isBlank()) {

			// @SuppressWarnings("null") // Eclipse bug with annotation arrays
			JStacheName name = findAnnotations(modelClass, JStacheConfig.class) //
					.flatMap(config -> Arrays.stream(config.naming())).findFirst().orElse(null);

			String prefix = name == null ? JStacheName.UNSPECIFIED : name.prefix();

			String suffix = name == null ? JStacheName.UNSPECIFIED : name.suffix();

			prefix = prefix.equals(JStacheName.UNSPECIFIED) ? JStacheName.DEFAULT_PREFIX : prefix;
			suffix = suffix.equals(JStacheName.UNSPECIFIED) ? JStacheName.DEFAULT_SUFFIX : suffix;

			cname = prefix + modelClass.getSimpleName() + suffix;
		}
		else {
			cname = a.name();
		}
		String packageName = modelClass.getPackageName();
		String fqn = packageName + (packageName.isEmpty() ? "" : ".") + cname;
		return fqn;
	}

	private static Charset resolveCharset(Class<?> c) {
		String charset = findAnnotations(c, JStacheConfig.class).map(config -> config.charset())
				.filter(cs -> !cs.isEmpty()).findFirst().orElse(null);
		if (charset == null) {
			return StandardCharsets.UTF_8;
		}
		return Charset.forName(charset);
	}

	static <A extends Annotation> Stream<A> findAnnotations(Class<?> c, Class<A> annotationClass) {
		var s = annotationElements(c);
		return s.filter(p -> p != null).flatMap(p -> Stream.ofNullable(p.getAnnotation(annotationClass)));
	}

	private static Stream<? extends AnnotatedElement> annotationElements(Class<?> c) {
		Stream<? extends AnnotatedElement> enclosing = enclosing(c).flatMap(Templates::expandUsing);
		return Stream.<Stream<? extends AnnotatedElement>>builder() //
				.add(enclosing) //
				.add(Stream.ofNullable(c.getPackage())) //
				.add(Stream.ofNullable(c.getModule())) //
				.build() //
				.flatMap(Function.identity());
	}

	/*
	 * This is to get referenced config of JStacheConfig.using
	 */
	private static Stream<Class<?>> expandUsing(Class<?> e) {

		JStacheConfig config = e.getAnnotation(JStacheConfig.class);
		if (config == null) {
			return Stream.of(e);
		}
		var using = config.using();
		if (!using.equals(void.class)) {
			return Stream.of(e, using);
		}
		return Stream.of(e);
	}

	private static Stream<Class<?>> enclosing(Class<?> e) {
		return findClasses(e, Class::getEnclosingClass);
	}

	private static Stream<Class<?>> parents(Class<?> e) {
		return findClasses(e, Class::getSuperclass);
	}

	private static Stream<Class<?>> interfaces(Class<?> clazz) {
		return Stream.concat(Stream.of(clazz.getInterfaces()),
				Stream.of(clazz.getInterfaces()).flatMap(interfaceClass -> interfaces(interfaceClass)));
	}

	private static Stream<Class<?>> findClasses(Class<?> e, Function<Class<?>, @Nullable Class<?>> f) {
		AbstractSpliterator<Class<?>> split = new AbstractSpliterator<Class<?>>(Long.MAX_VALUE, 0) {
			@Nullable
			Class<?> current = e;

			@Override
			public boolean tryAdvance(Consumer<? super Class<?>> action) {
				Class<?> c;
				if ((c = current) == null) {
					return false;
				}
				current = f.apply(c);
				action.accept(c);
				return true;
			}
		};
		return StreamSupport.stream(split, false);
	}

	private static <T> @Nullable Template<?> templateByServiceLoader(Class<T> clazz, ClassLoader classLoader,
			System.Logger logger) {
		ServiceLoader<TemplateProvider> loader = ServiceLoader.load(TemplateProvider.class, classLoader);
		return findTemplates(loader, TemplateConfig.empty(), e -> {
			logger.log(Level.ERROR, "Template provider failed to load. Skipping it.", e);
		}).filter(t -> clazz.equals(t.modelClass())).findFirst().orElse(null);
	}

	/**
	 * Find templates by the given service loader.
	 * @param serviceLoader a prepared service loader
	 * @param templateConfig template config to use for instantiating templates
	 * @param errorHandler handle {@link ServiceConfigurationError} errors.
	 * @return lazy stream of templates
	 */
	public static Stream<Template<?>> findTemplates( //
			ServiceLoader<TemplateProvider> serviceLoader, //
			TemplateConfig templateConfig, //
			Consumer<ServiceConfigurationError> errorHandler) {
		return serviceLoader.stream().flatMap(p -> {
			try {
				return p.get().provideTemplates().stream();
			}
			catch (ServiceConfigurationError e) {
				errorHandler.accept(e);
			}
			return Stream.empty();
		});
	}

	private static List<ClassLoader> collectClassLoaders(@Nullable ClassLoader classLoader) {
		return Stream.<@Nullable ClassLoader>builder().add(classLoader)
				.add(Thread.currentThread().getContextClassLoader()).add(Template.class.getClassLoader()).build()
				.<ClassLoader>flatMap(s -> Stream.ofNullable(s)).toList();
	}

	@SuppressWarnings("unchecked")
	static <E extends Throwable> void sneakyThrow(final Throwable x) throws E {
		throw (E) x;
	}

	/**
	 * Resolve path lookup information reflectively from a model class by doing config
	 * resolution at runtime.
	 * @param model a class annotated with JStache
	 * @return the resolved path annotation
	 * @apiNote This method is an implementation detail for reflection rendering engines
	 * such as JMustache and JStachio's future reflection based engine. It is recommended
	 * you do not rely on it as it is subject to change in the future.
	 * @deprecated use {@link #getInfoByReflection(Class)} or {@link #getPathInfo(Class)}.
	 */
	@Deprecated
	public static @Nullable JStachePath resolvePath(Class<?> model) {
		return _resolvePath(model);
	}

	/**
	 * <strong>INTERNAL (use at your own risk):</strong> Resolved {@link JStachePath}
	 * config by replacing {@link JStachePath#UNSPECIFIED} with default values. The passed
	 * in model class does not need be directly annotated with {@link JStache} and can be
	 * subclass.
	 * @param modelClass the model's class
	 * @return never <code>null</code> path information.
	 * @apiNote <strong>INTERNAL (use at your own risk):</strong> This method largely
	 * exists because {@link TemplateInfo} does not expose the original prefix and suffix
	 * used. The prefix and suffix are needed for reloading the templates for extensions.
	 * If you use this method please let the developers know and for what.
	 */
	public static PathInfo getPathInfo(Class<?> modelClass) {
		return DefaultPathInfo.of(modelClass, _resolvePath(modelClass));
	}

	private static @Nullable JStachePath _resolvePath(Class<?> model) {
		// TODO perhaps this information should be on TemplateInfo?
		return annotationElements(model) //
				.flatMap(e -> TemplateInfos.resolvePathOnElement(e).stream()) //
				.findFirst() //
				.orElse(null);
	}

	/**
	 * Resolved {@link JStachePath} config by replacing {@link JStachePath#UNSPECIFIED}
	 * with default values.
	 *
	 * @apiNote This interface largely exists because {@link TemplateInfo} does not expose
	 * the original prefix and suffix used.
	 * @author agentgt
	 */
	public sealed interface PathInfo {

		/**
		 * Resolved prefix.
		 * @return prefix maybe empty but will never be {@link JStachePath#UNSPECIFIED}.
		 */
		String prefix();

		/**
		 * Resolved suffix.
		 * @return suffix maybe empty but will never be {@link JStachePath#UNSPECIFIED}.
		 */
		String suffix();

		/**
		 * Calculates the expanded path and if the supplied path is empty then the path
		 * will be expanded based on the class name where package name are separated with
		 * a slash ("{@code /}") instead of a "<code>.</code>" and is suffixed with
		 * {@value JStachePath#AUTO_SUFFIX} if suffix is {@link JStachePath#UNSPECIFIED}.
		 * @param path if the path is empty it will be calculated based on the modelClass
		 * package name and class name.
		 * @return fully resolved path with prefix and suffix.
		 */
		public String resolveFullPath(String path);

	}

	record DefaultPathInfo(Class<?> modelClass, String prefix, String suffix, //
			boolean prefixUnspecified, boolean suffixUnspecified //
	) implements PathInfo {

		static DefaultPathInfo of(Class<?> modelClass, @Nullable JStachePath path) {
			String prefix, suffix;
			boolean prefixUnspecified, suffixUnspecified;
			if (path == null) {
				prefix = JStachePath.DEFAULT_PREFIX;
				suffix = JStachePath.DEFAULT_SUFFIX;
				prefixUnspecified = suffixUnspecified = true;
			}
			else {
				prefix = path.prefix();
				suffix = path.suffix();
				prefixUnspecified = JStachePath.UNSPECIFIED.equals(prefix);
				suffixUnspecified = JStachePath.UNSPECIFIED.equals(suffix);
				prefix = prefixUnspecified ? JStachePath.DEFAULT_PREFIX : prefix;
				suffix = suffixUnspecified ? JStachePath.DEFAULT_SUFFIX : suffix;
			}
			return new DefaultPathInfo(modelClass, requireNonNull(prefix), requireNonNull(suffix), prefixUnspecified,
					suffixUnspecified);
		}

		@Override
		public String resolveFullPath(String path) {
			String resolvePath;
			String prefix = prefix();
			String suffix = suffix();
			if (path.isEmpty()) {
				resolvePath = resolveDefaultPath(modelClass);
				if (suffixUnspecified()) {
					suffix = JStachePath.AUTO_SUFFIX;
				}
			}
			else {
				resolvePath = path;
			}
			return prefix + resolvePath + suffix;

		}

		private static String resolveDefaultPath(Class<?> model) {
			String resolvedPath;
			String folder = model.getPackageName().replace('.', '/');
			folder = folder.isEmpty() ? folder : folder + "/";
			resolvedPath = folder + model.getSimpleName();
			return resolvedPath;
		}
	}

	static class TemplateInfos {

		public static TemplateInfo templateOf(Class<?> model) throws Exception {
			JStache stache = model.getAnnotation(JStache.class);
			if (stache == null) {
				throw new IllegalArgumentException(
						"Model class is not annotated with " + JStache.class.getSimpleName() + ". class: " + model);
			}

			String templateString = stache.template();

			final String templateName = generatedClassName(model);
			String templatePath;
			if (!templateString.isEmpty()) {
				templatePath = "";
			}
			else {
				PathInfo pathInfo = getPathInfo(model);
				templatePath = pathInfo.resolveFullPath(requireNonNull(stache.path()));
			}

			var ee = EscaperProvider.INSTANCE.providesFromModelType(model, stache);
			Function<String, String> templateEscaper = ee.getValue();
			Class<?> templateContentType = ee.getKey();
			String templateMediaType = "";
			var jstacheContentType = templateContentType.getAnnotation(JStacheContentType.class);
			if (jstacheContentType != null) {
				templateMediaType = Objects.requireNonNull(jstacheContentType.mediaType());
			}
			Function<@Nullable Object, String> templateFormatter = FormatterProvider.INSTANCE
					.providesFromModelType(model, stache).getValue();

			Charset templateCharset = resolveCharset(model);

			long lastLoaded = System.currentTimeMillis();
			return new SimpleTemplateInfo( //
					templateName, //
					templatePath, //
					templateCharset, //
					templateMediaType, //
					templateString, //
					templateContentType, //
					templateEscaper, //
					templateFormatter, //
					lastLoaded, //
					model);

		}

		private static Optional<JStachePath> resolvePathOnElement(AnnotatedElement a) {
			var path = a.getAnnotation(JStachePath.class);
			if (path != null) {
				return Optional.of(path);
			}
			var config = a.getAnnotation(JStacheConfig.class);
			if (config != null && config.pathing().length > 0) {
				return Optional.ofNullable(config.pathing()[0]);
			}
			return Optional.empty();
		}

		sealed interface StaticProvider<P> {

			private @Nullable Class<?> autoToNull(@Nullable Class<?> type) {
				if ((type == null) || type.equals(autoProvider())) {
					return null;
				}
				return type;
			}

			default Class<?> nullToDefault(@Nullable Class<?> type) {
				Class<?> c = autoToNull(type);
				if (c == null) {
					return defaultProvider();
				}
				return c;
			}

			String providesMethod(Class<?> type);

			Class<?> autoProvider();

			Class<?> defaultProvider();

			@Nullable
			Class<?> providerFromJStache(JStache jstache);

			Class<?> providerFromConfig(JStacheConfig config);

			default Class<?> findProvider(Class<?> modelType, JStache jstache) {
				@Nullable
				Class<?> provider = autoToNull(providerFromJStache(jstache));
				if (provider != null) {
					return provider;
				}
				provider = findAnnotations(modelType, JStacheConfig.class)
						.flatMap(config -> Optional.ofNullable(autoToNull(providerFromConfig(config))).stream())
						.findFirst().orElse(null);
				return nullToDefault(provider);
			}

			default Entry<Class<?>, P> providesFromModelType(Class<?> modelType, JStache jstache) throws Exception {
				var t = findProvider(modelType, jstache);
				return Map.entry(t, Objects.requireNonNull(provides(t)));
			}

			@SuppressWarnings("unchecked")
			default P provides(Class<?> type) throws Exception {
				String provides = providesMethod(type);
				var method = type.getMethod(provides);
				Object r = method.invoke(provides);
				return (P) r;
			}

		}

		enum EscaperProvider implements StaticProvider<Function<String, String>> {

			INSTANCE;

			@Override
			public Class<?> autoProvider() {
				return UnspecifiedContentType.class;
			}

			@Override
			public Class<?> defaultProvider() {
				return Html.class;
			}

			@Override
			public @Nullable Class<?> providerFromJStache(JStache jstache) {
				return null;
			}

			@Override
			public Class<?> providerFromConfig(JStacheConfig config) {
				return Objects.requireNonNull(config.contentType());
			}

			@Override
			public String providesMethod(Class<?> type) {
				JStacheContentType a = type.getAnnotation(JStacheContentType.class);
				if (a == null) {
					throw new IllegalArgumentException("Specified content type class is not annotated with @"
							+ JStacheContentType.class.getSimpleName());
				}
				return Objects.requireNonNull(a.providesMethod());
			}

			@Override
			public Function<String, String> provides(@Nullable Class<?> contentType) throws Exception {
				contentType = nullToDefault(contentType);
				if (contentType.equals(Html.class)) {
					return Html.provider();
				}
				else if (contentType.equals(PlainText.class)) {
					return PlainText.provider();
				}
				return StaticProvider.super.provides(contentType);
			}

		}

		enum FormatterProvider implements StaticProvider<Function<@Nullable Object, String>> {

			INSTANCE;

			@Override
			public Class<?> autoProvider() {
				return UnspecifiedFormatter.class;
			}

			@Override
			public Class<?> defaultProvider() {
				return DefaultFormatter.class;
			}

			@Override
			public @Nullable Class<?> providerFromJStache(JStache jstache) {
				return null;
			}

			@Override
			public Class<?> providerFromConfig(JStacheConfig config) {
				return Objects.requireNonNull(config.formatter());
			}

			@Override
			public String providesMethod(Class<?> type) {
				JStacheFormatter a = type.getAnnotation(JStacheFormatter.class);
				if (a == null) {
					throw new IllegalArgumentException("Specified formatter provider is not annotated with @"
							+ JStacheFormatter.class.getSimpleName());
				}
				return Objects.requireNonNull(a.providesMethod());
			}

			@Override
			public Function<@Nullable Object, String> provides(@Nullable Class<?> formatterType) throws Exception {
				formatterType = nullToDefault(formatterType);
				if (formatterType.equals(DefaultFormatter.class)) {
					return DefaultFormatter.provider();
				}
				else if (formatterType.equals(SpecFormatter.class)) {
					return SpecFormatter.provider();
				}
				return StaticProvider.super.provides(formatterType);
			}

		}

		record SimpleTemplateInfo( //
				String templateName, //
				String templatePath, //
				Charset templateCharset, //
				String templateMediaType, //
				String templateString, //
				Class<?> templateContentType, //
				Function<String, String> templateEscaper, //
				Function<@Nullable Object, String> templateFormatter, //
				long lastLoaded, //
				Class<?> modelClass) implements TemplateInfo {

			@Override
			public boolean supportsType(Class<?> type) {
				return modelClass().isAssignableFrom(type);
			}

			@Override
			public Function<String, String> templateEscaper() {
				return this.templateEscaper;
			}

			@Override
			public Function<@Nullable Object, String> templateFormatter() {
				return this.templateFormatter;
			}

		}

	}

}