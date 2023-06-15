package io.jstach.jstachio.spi;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.jdt.annotation.NonNull;
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
	 * {@link TemplateInfo} based on annotation metadata. This method is effectively calls
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
		EnumSet<TemplateLoadStrategy> strategies = EnumSet.noneOf(TemplateLoadStrategy.class);

		for (var s : ALL_STRATEGIES) {
			if (s.isEnabled(config)) {
				strategies.add(s);
			}
		}
		var classLoaders = collectClassLoaders(modelType.getClassLoader());

		Logger logger = config.getLogger(Templates.class.getCanonicalName());

		Exception error;
		try {
			Template<?> r = Templates.getTemplate(modelType, strategies, classLoaders, logger);
			return r;
		}
		catch (Exception e) {
			error = e;
		}
		if (!config.getBoolean(JStachioConfig.REFLECTION_TEMPLATE_DISABLE)) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING,
						"Could not find generated template and will try reflection for model type: " + modelType,
						error);
			}
			return getInfoByReflection(modelType);

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
	 * Finds a template by reflection or an exception is thrown.
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
	 * Strategy to load templates dynamically.
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

		protected abstract <T> @Nullable Template<T> load(Class<T> clazz, ClassLoader classLoader, System.Logger logger)
				throws Exception;

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
		if (a == null || a.name().isBlank()) {

			JStacheName name = findAnnotations(modelClass, JStacheConfig.class) //
					.flatMap(config -> Stream.of(config.naming())).findFirst().orElse(null);

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
		return s.filter(p -> p != null).map(p -> p.getAnnotation(annotationClass)).filter(a -> a != null);
	}

	private static @NonNull Stream<AnnotatedElement> annotationElements(Class<?> c) {
		Stream<? extends AnnotatedElement> enclosing = enclosing(c).flatMap(Templates::expandUsing);
		var s = Stream.concat(enclosing, Stream.of(c.getPackage(), c.getModule()));
		return s;
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
		AbstractSpliterator<Class<?>> split = new AbstractSpliterator<Class<?>>(Long.MAX_VALUE, 0) {
			@Nullable
			Class<?> current = e;

			@Override
			public boolean tryAdvance(Consumer<? super Class<?>> action) {
				if (current == null) {
					return false;
				}
				var c = current;
				current = current.getEnclosingClass();
				action.accept(c);
				return true;
			}
		};
		return StreamSupport.stream(split, false);
	}

	private static <T> @Nullable Template<?> templateByServiceLoader(Class<T> clazz, ClassLoader classLoader,
			System.Logger logger) {
		ServiceLoader<TemplateProvider> loader = ServiceLoader.load(TemplateProvider.class, classLoader);
		return findTemplates(loader, e -> {
			logger.log(Level.ERROR, "Template provider failed to load. Skipping it.", e);
		}).filter(t -> t.supportsType(clazz)).findFirst().orElse(null);
	}

	/**
	 * Find templates by the given service loader.
	 * @param serviceLoader a prepared service loader
	 * @param errorHandler handle {@link ServiceConfigurationError} errors.
	 * @return lazy stream of templates
	 */
	public static Stream<Template<?>> findTemplates(ServiceLoader<TemplateProvider> serviceLoader,
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
		return Stream.of(classLoader, Thread.currentThread().getContextClassLoader(), Template.class.getClassLoader())
				.filter(cl -> cl != null).toList();
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
	 */
	public static @Nullable JStachePath resolvePath(Class<?> model) {
		// TODO perhaps this information should be on TemplateInfo?
		return annotationElements(model).map(TemplateInfos::resolvePathOnElement).filter(p -> p != null).findFirst()
				.orElse(null);
	}

	static class TemplateInfos {

		public static TemplateInfo templateOf(Class<?> model) throws Exception {
			JStache stache = model.getAnnotation(JStache.class);
			if (stache == null) {
				throw new IllegalArgumentException(
						"Model class is not annotated with " + JStache.class.getSimpleName() + ". class: " + model);
			}
			@Nullable
			JStachePath pathConfig = resolvePath(model);
			String templateString = stache.template();

			final String templateName = generatedClassName(model);
			String path = stache.path();
			String templatePath;
			if (templateString.isEmpty() && path.isEmpty()) {
				String folder = model.getPackageName().replace('.', '/');
				folder = folder.isEmpty() ? folder : folder + "/";
				templatePath = folder + model.getSimpleName();
			}
			else if (!path.isEmpty()) {
				templatePath = path;
			}
			else {
				templatePath = "";
			}
			if (pathConfig != null && !templatePath.isBlank()) {
				templatePath = pathConfig.prefix() + templatePath + pathConfig.suffix();
			}

			// Class<?> templateContentType =
			// EscaperProvider.INSTANCE.nullToDefault(stache.contentType());

			var ee = EscaperProvider.INSTANCE.providesFromModelType(model, stache);
			Function<String, String> templateEscaper = ee.getValue();
			Class<?> templateContentType = ee.getKey();
			String templateMediaType = "";
			var jstacheContentType = templateContentType.getAnnotation(JStacheContentType.class);
			if (jstacheContentType != null) {
				templateMediaType = jstacheContentType.mediaType();
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

		private static @Nullable JStachePath resolvePathOnElement(AnnotatedElement a) {
			var path = a.getAnnotation(JStachePath.class);
			if (path != null) {
				return path;
			}
			var config = a.getAnnotation(JStacheConfig.class);
			if (config != null && config.pathing().length > 0) {
				return config.pathing()[0];
			}
			return null;
		}

		sealed interface StaticProvider<P> {

			default @Nullable Class<?> autoToNull(@Nullable Class<?> type) {
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
						.map(config -> autoToNull(providerFromConfig(config))).filter(p -> p != null).findFirst()
						.orElse(null);
				return nullToDefault(provider);
			}

			default Entry<Class<?>, P> providesFromModelType(Class<?> modelType, JStache jstache) throws Exception {
				var t = findProvider(modelType, jstache);
				return Map.entry(t, provides(t));
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
				return config.contentType();
			}

			@Override
			public String providesMethod(Class<?> type) {
				JStacheContentType a = type.getAnnotation(JStacheContentType.class);
				return a.providesMethod();
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
				return config.formatter();
			}

			@Override
			public String providesMethod(Class<?> type) {
				JStacheFormatter a = type.getAnnotation(JStacheFormatter.class);
				return a.providesMethod();
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

		}

	}

}