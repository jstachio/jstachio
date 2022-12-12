package io.jstach.examples.nullable;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import io.jstach.jstache.JStache;

public class ModelWithNullableTest {

	@Test
	public void testNullableAnnotationsPropagated() throws IOException {
		/*
		 * Yeah this is lame and brittle but whatever
		 */
		String filePath = ModelWithNullable.class.getName().replace('.', '/') + JStache.IMPLEMENTATION_SUFFIX + ".java";
		String java = Files.readString(Path.of("target/generated-sources/annotations/" + filePath),
				StandardCharsets.UTF_8);
		String iterable = "java.util.Iterator<? extends java.lang.@org.eclipse.jdt.annotation.Nullable String>";
		String element = "java.lang.@org.eclipse.jdt.annotation.Nullable String element";
		assertTrue(java.contains(iterable));
		assertTrue(java.contains(element));
	}

}
