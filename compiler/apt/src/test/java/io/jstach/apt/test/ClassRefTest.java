package io.jstach.apt.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

import io.jstach.apt.internal.util.ClassRef;

public class ClassRefTest {

	@Test
	public void testOfBinaryName() {
		ClassRef ref = ClassRef.ofBinaryName("java.util.Map$Entry");

		assertEqualsNullCheck("Entry", ref.getSimpleName());

		assertEqualsNullCheck("java.util.Map.Entry", ref.getCanonicalName());
		assertEqualsNullCheck("java.util", ref.getPackageName());
	}

	@Test
	public void testOfStringString() {
		ClassRef ref = ClassRef.of("java.util", "Map.Entry");
		assertEqualsNullCheck("Entry", ref.getSimpleName());
		assertEqualsNullCheck("java.util.Map.Entry", ref.getCanonicalName());
		assertEqualsNullCheck("java.util", ref.getPackageName());
	}

	private static void assertEqualsNullCheck(String expected, @Nullable String actual) {
		/*
		 * Checkers defaults for JUnit are fucking annoying but I'm too lazy to go switch
		 * out the stub
		 */
		if (actual == null) {
			fail("actual is null");
		}
		else {
			assertEquals(expected, actual);
		}
	}

}
