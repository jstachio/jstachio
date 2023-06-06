package io.jstach.examples.nullable;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

import io.jstach.examples.util.SourceCodeFinder;

public class NullableAnnotationTest {

	private static final String GENERATED_PATH = "target/generated-sources/annotations";

	@Test
	public void testAnnotatedWithNullable() throws IOException {
		String code = SourceCodeFinder.code(Path.of(GENERATED_PATH), NullCheckAlwaysModel.class);
		assertTrue(code.contains(Nullable.class.getName()));
		assertFalse(code.contains("/* @Nullable */"));
	}

	@Test
	public void testCommentedNullable() throws Exception {
		String code = SourceCodeFinder.code(Path.of(GENERATED_PATH), ModelWithNullable.class);
		/*
		 * Shows how the type use annotation was copied from the original type.
		 *
		 * TODO maybe an option to replace or ignore the nullable annotation in the future
		 * with the rare possibility of models that have different nullable annotations.
		 * This is an unlikely use case.
		 */
		assertTrue(
				code.contains("java.util.Iterator<? extends java.lang.@org.eclipse.jdt.annotation.Nullable String>"));
		/*
		 * We should have comments instead of nullable annotations for this generated clas
		 */
		assertTrue(code.contains("/* @Nullable */"));
	}

}
