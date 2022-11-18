package io.jstach.jstachio.spi;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheContentType;
import io.jstach.jstache.JStacheContentType.AutoContentType;
import io.jstach.jstache.JStacheFormatter;
import io.jstach.jstache.JStacheFormatter.AutoFormatter;
import io.jstach.jstache.JStacheFormatterTypes;
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
 * This utility class is useful if you plan on implementing your own {@link JStachio}.
 *
 * @author agentgt
 *
 */
public final class Templates {

	private Templates() {
	}

	/**
	 * Finds a {@link Template} if possible otherwise falling back to a
	 * {@link TemplateInfo} based on annotation metadata.
	 * @apiNote Callers can do an <code>instanceof Template t</code> to see if a generated
	 * template was returned instead of the fallback.
	 * @param modelType the models class (<em>the one annotated with {@link JStache} and
	 * not the Templates class</em>)
	 * @param config config used to determine whether or not to fallback
	 * @return the template info which might be a {@link Template} if the generated
	 * template was found.
	 * @throws Exception if any reflection error happes or the template is not found
	 */
	public static TemplateInfo findTemplate(Class<?> modelType, JStacheConfig config) throws Exception {
		Exception error;
		try {
			Template<?> r = Templates.getTemplate(modelType);
			return r;
		}
		catch (Exception e) {
			error = e;
		}
		if (!config.getBoolean(JStacheConfig.REFLECTION_TEMPLATE_DISABLE)) {
			Logger logger = config.getLogger(JStacheServices.class.getCanonicalName());
			if (logger.isLoggable(Level.WARNING)) {
				logger.log(Level.WARNING,
						"Could not find generated template and will try reflection for model type: " + modelType,
						error);
			}
			return TemplateInfos.templateOf(modelType);

		}
		throw error;

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
		if (a != null && !":auto".equals(a.adapterName())) {
			cname = a.adapterName();
		}
		else {
			cname = c.getSimpleName() + Template.IMPLEMENTATION_SUFFIX;
		}
		String packageName = c.getPackageName();
		String fqn = packageName + (packageName.isEmpty() ? "" : ".") + cname;
		return fqn;
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

	private static List<ClassLoader> collectClassLoaders(ClassLoader classLoader) {
		List<ClassLoader> classLoaders = new ArrayList<>(3);
		classLoaders.add(classLoader);

		if (Thread.currentThread().getContextClassLoader() != null) {
			classLoaders.add(Thread.currentThread().getContextClassLoader());
		}

		classLoaders.add(Template.class.getClassLoader());

		return classLoaders;
	}

	static class TemplateInfos {

		public static TemplateInfo templateOf(Class<?> model) throws Exception {
			JStache stache = model.getAnnotation(JStache.class);
			if (stache == null) {
				throw new IllegalArgumentException(
						"Model class is not annotated with " + JStache.class.getSimpleName() + ". class: " + model);
			}
			JStachePath path = getAnnotation(JStachePath.class, model);

			String templateName = stache.adapterName();
			String templatePath = stache.path();
			if (path != null) {
				templatePath = path.prefix() + templatePath + path.suffix();
			}
			String templateString = stache.template();

			Class<?> templateContentType = EscaperProvider.INSTANCE.nullToDefault(stache.contentType());

			Function<String, String> templateEscaper = EscaperProvider.INSTANCE.provides(templateContentType);

			Class<?> formatterProvider = FormatterProvider.INSTANCE.autoToNull(stache.formatter());

			if (formatterProvider == null) {
				JStacheFormatterTypes formatterTypes = getAnnotation(JStacheFormatterTypes.class, model);
				if (formatterTypes != null) {
					formatterProvider = FormatterProvider.INSTANCE.autoToNull(formatterTypes.formatter());
				}
			}

			Function<@Nullable Object, String> templateFormatter = FormatterProvider.INSTANCE
					.provides(formatterProvider);

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

		static <A extends Annotation> @Nullable A getAnnotation(Class<A> annotation, Class<?> model) {
			var a = model.getAnnotation(annotation);
			if (a == null) {
				a = model.getPackage().getAnnotation(annotation);
			}
			return a;
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

			@SuppressWarnings("unchecked")
			default P provides(@Nullable Class<?> type) throws Exception {
				type = nullToDefault(type);
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
				return AutoContentType.class;
			}

			@Override
			public Class<?> defaultProvider() {
				return Html.class;
			}

			@Override
			public String providesMethod(Class<?> type) {
				JStacheContentType a = type.getAnnotation(JStacheContentType.class);
				return a.providesMethod();
			}

			public Function<String, String> provides(@Nullable Class<?> contentType) throws Exception {
				contentType = nullToDefault(contentType);
				if (contentType.equals(Html.class)) {
					return Html.provides();
				}
				else if (contentType.equals(PlainText.class)) {
					return PlainText.provides();
				}
				return StaticProvider.super.provides(contentType);
			}

		}

		enum FormatterProvider implements StaticProvider<Function<@Nullable Object, String>> {

			INSTANCE;

			@Override
			public Class<?> autoProvider() {
				return AutoFormatter.class;
			}

			@Override
			public Class<?> defaultProvider() {
				return DefaultFormatter.class;
			}

			@Override
			public String providesMethod(Class<?> type) {
				JStacheFormatter a = type.getAnnotation(JStacheFormatter.class);
				return a.providesMethod();
			}

			public Function<@Nullable Object, String> provides(@Nullable Class<?> formatterType) throws Exception {
				formatterType = nullToDefault(formatterType);
				if (formatterType.equals(DefaultFormatter.class)) {
					return DefaultFormatter.provides();
				}
				else if (formatterType.equals(SpecFormatter.class)) {
					return SpecFormatter.provides();
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