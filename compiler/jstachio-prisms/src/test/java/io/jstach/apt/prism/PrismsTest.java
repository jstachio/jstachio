package io.jstach.apt.prism;

import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

import com.samskivert.mustache.Mustache;

import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheContentType.AutoContentType;
import io.jstach.jstache.JStacheFormatter.AutoFormatter;
import io.jstach.jstachio.Appender;
import io.jstach.jstachio.Escaper;
import io.jstach.jstachio.Formatter;
import io.jstach.jstachio.Renderer;
import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateInfo;
import io.jstach.jstachio.context.ContextNode;
import io.jstach.jstachio.escapers.Html;
import io.jstach.jstachio.escapers.PlainText;
import io.jstach.jstachio.formatters.DefaultFormatter;
import io.jstach.jstachio.spi.JStachioFilter.FilterChain;
import io.jstach.jstachio.spi.JStachioServices;
import io.jstach.jstachio.spi.TemplateProvider;

public class PrismsTest {

	record CodeModel(List<ClassModel> annotations, List<ClassModel> apiClasses) {
		JStacheFlags.Flag[] flags() {
			return JStacheFlags.Flag.values();
		}

		String rendererSuffix() {
			return Renderer.IMPLEMENTATION_SUFFIX;
		}
	}

	record ClassModel(Class<?> klass) {
		String constant() {
			return convertCamelToSnake(klass.getSimpleName()) + "_CLASS";
		}

		String name() {
			return klass.getCanonicalName();
		}
	}

	@Test
	public void testCode() throws Exception {
		String t = """
				package io.jstach.apt.prism;

				import java.util.List;

				import org.eclipse.jdt.annotation.NonNullByDefault;

				/**
				 * THIS CLASS IS GENERATED FROM PrismsTest. Run the test and copy and paste.
				 *
				 * Prisms are because we cannot load the annotation classes or the api classes in the
				 * annotation processor because of potential class loading issues.
				 *
				 * @author agentgt
				 *
				 */
				public interface Prisms {

					public static final String IMPLEMENTATION_SUFFIX = "{{rendererSuffix}}";

					@NonNullByDefault
					public enum Flag {

					{{#flags}}
						{{.}}, //
					{{/flags}}

					}

					/* API classes */
					{{#apiClasses}}
					public static final String {{constant}} = "{{name}}";

					{{/apiClasses}}
					/* Annotation classes */
					{{#annotations}}
					public static final String {{constant}} = "{{name}}";

					{{/annotations}}
					public static final List<String> ANNOTATIONS = List.of( //
					{{#annotations}}
							{{constant}}{{^-last}},{{/-last}} //
					{{/annotations}}
					);

				}
				""";
		List<ClassModel> annotations = allAnnotations().stream().map(ClassModel::new).toList();
		List<ClassModel> apiClasses = apiClasses().stream().map(ClassModel::new).toList();
		var model = new CodeModel(annotations, apiClasses);
		String expected = Mustache.compiler().escapeHTML(false).compile(t).execute(model);
		System.out.println(expected);

		String sourceFile = "src/main/java/" + Prisms.class.getCanonicalName().replace(".", "/") + ".java";
		String actual = Files.readString(Path.of(sourceFile));

		assertEquals(sourceFile + " should be up to date", expected, actual);
	}

	static String convertCamelToSnake(String s) {
		return camelCaseToScreamingSnake(s);

	}

	private static List<Class<?>> apiClasses() {
		return List.of( //
				Renderer.class, //
				Template.class, //
				TemplateProvider.class, //
				Appender.class, //
				Escaper.class, //
				Formatter.class, //
				DefaultFormatter.class, //
				TemplateInfo.class, //
				FilterChain.class, //
				JStachioServices.class, //
				ContextNode.class, //
				AutoFormatter.class, //
				AutoContentType.class, //
				Html.class, //
				PlainText.class //

		);
	}

	private static List<Class<?>> allAnnotations() {
		return List.of(//
				io.jstach.jstache.JStaches.class, //
				io.jstach.jstache.JStache.class, //
				io.jstach.jstache.JStachePath.class, //
				io.jstach.jstache.JStacheInterfaces.class, //
				io.jstach.jstache.JStachePartials.class, //
				io.jstach.jstache.JStachePartial.class, //
				io.jstach.jstache.JStacheLambda.class, //
				io.jstach.jstache.JStacheLambda.Raw.class, //
				io.jstach.jstache.JStacheContentType.class, //
				io.jstach.jstache.JStacheFormatter.class, //
				io.jstach.jstache.JStacheFormatterTypes.class, //
				io.jstach.jstache.JStacheFlags.class //
		);
	}

	private static String camelCaseToScreamingSnake(String name) {
		StringBuilder sb = new StringBuilder();
		splitPascalCase(name, new NameSplitConsumer() {
			@Override
			public void consume(CharSequence cs, int start, int end, int count) {
				if (count != 0) {
					sb.append("_");
				}
				for (int i = start; i < end; i++) {
					sb.append(Character.toUpperCase(cs.charAt(i)));
				}

			}
		});
		return sb.toString();
	}

	/*
	 * Adapted from SnapHop code base Apache License (c) 2022 Adam Gent, Wen Tian
	 */
	private static void splitPascalCase(String name, NameSplitConsumer consumer) {
		int start = 0;
		int end = name.length();

		if (start > end) {
			throw new IllegalArgumentException("start index greater than end");
		}
		int j = 0;
		int currentType = Character.getType(name.charAt(start));
		for (int i = start; i < end; i++) {
			final int type = Character.getType(name.charAt(i));
			if (type == currentType) {
				continue;
			}

			if (type == Character.LOWERCASE_LETTER && currentType == Character.UPPERCASE_LETTER) {
				currentType = type;
				continue;
			}

			consumer.consume(name, start, i, j++);
			start = i;
			currentType = type;
		}
		consumer.consume(name, start, end, j);
	}

	private interface NameSplitConsumer {

		public void consume(CharSequence cs, int start, int end, int count);

	}

}
