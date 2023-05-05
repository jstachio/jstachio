package io.jstach.examples.nullable;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

import io.jstach.jstache.JStacheName;
import io.jstach.jstachio.TemplateConfig;

public class ModelWithNullableTest {

	@Test
	public void testNullableAnnotationsPropagated() throws IOException {
		/*
		 * Yeah this is lame and brittle but whatever
		 */
		String filePath = ModelWithNullable.class.getName().replace('.', '/') + JStacheName.DEFAULT_SUFFIX + ".java";
		String java = Files.readString(Path.of("target/generated-sources/annotations/" + filePath),
				StandardCharsets.UTF_8);
		String iterable = "java.util.Iterator<? extends java.lang.@org.eclipse.jdt.annotation.Nullable String>";
		String element = "java.lang.@org.eclipse.jdt.annotation.Nullable String element";
		assertTrue(java.contains(iterable));
		assertTrue(java.contains(element));
	}

	@Test
	public void testNullable() throws Exception {
		List<String> names = List.of("Eric", "Kenny", "Kyle");
		List<@Nullable String> namesNullable = new ArrayList<>();
		namesNullable.add("Eric");
		namesNullable.add(null); // Kenny
		namesNullable.add("Stan");
		@Nullable
		List<String> nullableNames = null;

		@Nullable
		String @Nullable [] ids = null;

		@Nullable
		String nullableMessage = null;
		String message = "Stuff";

		ModelWithNullable m = new ModelWithNullable(names, namesNullable, nullableNames, ids, nullableMessage, message);

		String actual = new ModelWithNullableRenderer(new TemplateConfig() {
			@Override
			public @Nullable Function<@Nullable Object, String> formatter() {
				return (s -> s == null ? "NULL" : String.valueOf(s));
			}
		}).execute(m);

		String expected = """
				Names:
				Eric
				Kenny
				Kyle

				NullableNames:

				NamesNullable:
				Eric
				Name missing
				Stan

				Ids:

				NullableMessage:
				NULL

				NullableMessage Dot:


				NullableMessage Condition:

				NullableMessage Invert:
				nullableMessage was null!

				Message:
				Stuff

				Message Dot:
				5

				Message Condition:
				Stuff
				""";

		assertEquals(expected, actual);
	}

	@Test
	public void testNullCheck() throws Exception {

		List<@Nullable String> namesNullable = new ArrayList<>();
		namesNullable.add("Eric");
		namesNullable.add(null); // Kenny
		namesNullable.add("Stan");

		NullCheckAlwaysModel m = new NullCheckAlwaysModel(namesNullable);
		String actual = NullCheckAlwaysModelRenderer.of().execute(m);
		String expected = """
				---------
				Eric
				Index: 0
				---------
				Missing for index: 1
				Index: 1
				---------
				Stan
				Index: 2
				---------
				""";
		assertEquals(expected, actual);
	}

}
