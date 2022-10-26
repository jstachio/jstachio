package io.jstach.apt.test;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import io.jstach.Appender;
import io.jstach.Escaper;
import io.jstach.Formatter;
import io.jstach.RenderFunction;
import io.jstach.Renderable;
import io.jstach.Renderer;
import io.jstach.context.ContextNode;
import io.jstach.escapers.Html;
import io.jstach.spi.JStacheServices;

public class AnnotationListingTest {

	@Test
	public void testList() throws Exception {
		StringBuilder sb = new StringBuilder();

		for (var a : allAnnotations()) {
			sb.append("\tpublic static final String " + convertCamelToSnake(a.getSimpleName()) + "_CLASS = \""
					+ a.getCanonicalName() + "\";\n");
		}
		sb.append("\n");
		sb.append("List.of( //\n");

		String lines = allAnnotations().stream() //
				.map(Class::getCanonicalName) //
				.map(s -> "\t\"" + s + "\"").collect(Collectors.joining(", //\n"));
		sb.append(lines);
		sb.append(" //\n);");
		sb.append("\n");
		System.out.println(sb.toString());
		String expected = """
					public static final String JSTACHES_CLASS = "io.jstach.annotation.JStaches";
					public static final String JSTACHE_CLASS = "io.jstach.annotation.JStache";
					public static final String JSTACHEPATH_CLASS = "io.jstach.annotation.JStachePath";
					public static final String JSTACHEINTERFACES_CLASS = "io.jstach.annotation.JStacheInterfaces";
					public static final String JSTACHEPARTIALS_CLASS = "io.jstach.annotation.JStachePartials";
					public static final String JSTACHEPARTIAL_CLASS = "io.jstach.annotation.JStachePartial";
					public static final String JSTACHELAMBDA_CLASS = "io.jstach.annotation.JStacheLambda";
					public static final String RAW_CLASS = "io.jstach.annotation.JStacheLambda.Raw";
					public static final String JSTACHECONTENTTYPE_CLASS = "io.jstach.annotation.JStacheContentType";
					public static final String JSTACHEFORMATTERTYPES_CLASS = "io.jstach.annotation.JStacheFormatterTypes";
					public static final String JSTACHEFLAGS_CLASS = "io.jstach.annotation.JStacheFlags";

				List.of( //
					"io.jstach.annotation.JStaches", //
					"io.jstach.annotation.JStache", //
					"io.jstach.annotation.JStachePath", //
					"io.jstach.annotation.JStacheInterfaces", //
					"io.jstach.annotation.JStachePartials", //
					"io.jstach.annotation.JStachePartial", //
					"io.jstach.annotation.JStacheLambda", //
					"io.jstach.annotation.JStacheLambda.Raw", //
					"io.jstach.annotation.JStacheContentType", //
					"io.jstach.annotation.JStacheFormatterTypes", //
					"io.jstach.annotation.JStacheFlags" //
				);
												""";
		assertEquals(expected, sb.toString());

	}

	@Test
	public void testApiClasses() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (var a : apiClasses()) {
			sb.append("\tpublic static final String " + convertCamelToSnake(a.getSimpleName()) + "_CLASS = \""
					+ a.getCanonicalName() + "\";\n");
		}
		System.out.println(sb.toString());
	}

	static String convertCamelToSnake(String s) {
		return s.toUpperCase();
		// return s.replaceAll("(.)(\\p{Upper})", "$1_$2").toUpperCase();

	}

	private static List<Class<?>> apiClasses() {
		return List.of( //
				Renderer.class, //
				Appender.class, //
				Escaper.class, //
				Formatter.class, //
				RenderFunction.class, //
				Renderable.class, //
				JStacheServices.class, //
				ContextNode.class, //
				Html.class //
		);
	}

	private static List<Class<?>> allAnnotations() {
		return List.of(//
				io.jstach.annotation.JStaches.class, //
				io.jstach.annotation.JStache.class, //
				io.jstach.annotation.JStachePath.class, //
				io.jstach.annotation.JStacheInterfaces.class, //
				io.jstach.annotation.JStachePartials.class, //
				io.jstach.annotation.JStachePartial.class, //
				io.jstach.annotation.JStacheLambda.class, //
				io.jstach.annotation.JStacheLambda.Raw.class, //
				io.jstach.annotation.JStacheContentType.class, //
				io.jstach.annotation.JStacheFormatterTypes.class, //
				io.jstach.annotation.JStacheFlags.class //
		);
	}

}
