package io.jstach.spi;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.TemplateInfo;
import io.jstach.annotation.JStache;
import io.jstach.annotation.JStacheContentType;
import io.jstach.annotation.JStacheContentType.AutoContentType;
import io.jstach.annotation.JStacheFormatter;
import io.jstach.annotation.JStacheFormatter.AutoFormatter;
import io.jstach.annotation.JStacheFormatterTypes;
import io.jstach.annotation.JStachePath;
import io.jstach.escapers.Html;
import io.jstach.escapers.PlainText;
import io.jstach.formatters.DefaultFormatter;
import io.jstach.formatters.SpecFormatter;

/*
 * TODO move this to its own module that will be loaded by the service loader.
 */
class TemplateInfos {

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

		Function<@Nullable Object, String> templateFormatter = FormatterProvider.INSTANCE.provides(formatterProvider);

		return new SimpleTemplateInfo(templateName, templatePath, templateString, templateContentType, templateEscaper,
				templateFormatter);

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
			Function<@Nullable Object, String> templateFormatter //
	) implements TemplateInfo {

	}

}
