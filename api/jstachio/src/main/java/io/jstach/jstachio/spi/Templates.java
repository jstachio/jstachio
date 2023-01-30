package io.jstach.jstachio.spi;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
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
import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateInfo;
import io.jstach.jstachio.escapers.Html;
import io.jstach.jstachio.escapers.PlainText;
import io.jstach.jstachio.formatters.DefaultFormatter;
import io.jstach.jstachio.formatters.SpecFormatter;

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
		Exception error;
		try {
			Template<?> r = Templates.getTemplate(modelType);
			return r;
		}
		catch (Exception e) {
			error = e;
		}
		if (!config.getBoolean(JStachioConfig.REFLECTION_TEMPLATE_DISABLE)) {
			Logger logger = config.getLogger(JStachioExtension.class.getCanonicalName());
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
	 * @throws ClassNotFoundException if the template is not found
	 * @throws Exception if the template is not found or any reflective access errors
	 */
	public static <T> Template<T> getTemplate(Class<T> clazz) throws Exception {
		List<ClassLoader> classLoaders = collectClassLoaders(clazz.getClassLoader());
		return (Template<T>) getTemplate(clazz, classLoaders);
	}

	private static <T> Template<T> getTemplate(Class<T> templateType, Iterable<ClassLoader> classLoaders)
			throws Exception {

		for (ClassLoader classLoader : classLoaders) {
			Template<T> template = doGetTemplate(templateType, classLoader);
			if (template != null) {
				return template;
			}
		}

		throw new ClassNotFoundException("Cannot find implementation for " + templateType.getName());
	}

	@SuppressWarnings("unchecked")
	private static <T> @Nullable Template<T> doGetTemplate(Class<T> clazz, ClassLoader classLoader) throws Exception {
		try {
			Class<?> implementation = (Class<?>) classLoader.loadClass(resolveName(clazz));
			Constructor<?> constructor = implementation.getDeclaredConstructor();
			constructor.setAccessible(true);

			return (Template<T>) constructor.newInstance();
		}
		catch (ClassNotFoundException e) {
			return (Template<T>) getTemplateFromServiceLoader(clazz, classLoader);
		}
	}

	private static String resolveName(Class<?> c) {
		var a = c.getAnnotation(JStache.class);
		String cname;
		if (a == null || a.name().isBlank()) {

			JStacheName name = findAnnotations(c, JStacheConfig.class).flatMap(config -> Stream.of(config.naming()))
					.findFirst().orElse(null);

			String prefix = name == null ? JStacheName.UNSPECIFIED : name.prefix();

			String suffix = name == null ? JStacheName.UNSPECIFIED : name.suffix();

			prefix = prefix.equals(JStacheName.UNSPECIFIED) ? JStacheName.DEFAULT_PREFIX : prefix;
			suffix = suffix.equals(JStacheName.UNSPECIFIED) ? JStacheName.DEFAULT_SUFFIX : suffix;

			cname = prefix + c.getSimpleName() + suffix;
		}
		else {
			cname = a.name();
		}
		String packageName = c.getPackageName();
		String fqn = packageName + (packageName.isEmpty() ? "" : ".") + cname;
		return fqn;
	}

	private static <A extends Annotation> Stream<A> findAnnotations(Class<?> c, Class<A> annotationClass) {
		var s = annotationElements(c);
		return s.filter(p -> p != null).map(p -> p.getAnnotation(annotationClass)).filter(a -> a != null);
	}

	private static @NonNull Stream<AnnotatedElement> annotationElements(Class<?> c) {
		Stream<? extends AnnotatedElement> enclosing = enclosing(c);
		var s = Stream.concat(enclosing, Stream.of(c.getPackage(), c.getModule()));
		return s;
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

	private static <T> @Nullable Template<?> getTemplateFromServiceLoader(Class<T> clazz, ClassLoader classLoader) {
		ServiceLoader<TemplateProvider> loader = ServiceLoader.load(TemplateProvider.class, classLoader);
		for (TemplateProvider rp : loader) {
			for (var t : rp.provideTemplates()) {
				if (t.supportsType(clazz)) {
					return t;
				}
			}
		}
		return null;
	}

	private static List<ClassLoader> collectClassLoaders(@Nullable ClassLoader classLoader) {
		return Stream.of(classLoader, Thread.currentThread().getContextClassLoader(), Template.class.getClassLoader())
				.filter(cl -> cl != null).toList();
	}

	@SuppressWarnings("unchecked")
	static <E extends Throwable> void sneakyThrow(final Throwable x) throws E {
		throw (E) x;
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

			final String templateName = resolveName(model);
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

			Function<@Nullable Object, String> templateFormatter = FormatterProvider.INSTANCE
					.providesFromModelType(model, stache).getValue();

			long lastLoaded = System.currentTimeMillis();
			return new SimpleTemplateInfo( //
					templateName, //
					templatePath, //
					templateString, //
					templateContentType, //
					templateEscaper, //
					templateFormatter, //
					lastLoaded, //
					model);

		}

		private static JStachePath resolvePath(Class<?> model) {
			return annotationElements(model).map(TemplateInfos::resolvePathOnElement).filter(p -> p != null).findFirst()
					.orElse(null);
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
				if (type == null) {
					return null;
				}
				if (type.equals(autoProvider())) {
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