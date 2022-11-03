package io.jstach.apt.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.jstach.apt.internal.util.ClassRef;

public class ClassRefTest {

	@Test
	public void testOfBinaryName() {
		ClassRef ref = ClassRef.ofBinaryName("java.util.Map$Entry");

		assertEquals("Entry", ref.getSimpleName());
		assertEquals("java.util.Map.Entry", ref.getCanonicalName());
		assertEquals("java.util", ref.getPackageName());
	}

	@Test
	public void testOfStringString() {
		ClassRef ref = ClassRef.of("java.util", "Map.Entry");
		assertEquals("Entry", ref.getSimpleName());
		assertEquals("java.util.Map.Entry", ref.getCanonicalName());
		assertEquals("java.util", ref.getPackageName());
	}

}
