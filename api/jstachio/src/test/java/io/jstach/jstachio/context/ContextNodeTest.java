package io.jstach.jstachio.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

public class ContextNodeTest {

	@Test
	public void testIsFalseyArray() {
		int[] ia = new int[] { 1, 0 };
		assertFalse(ContextNode.isFalsey(ia));
		assertTrue(ContextNode.ofRoot(ia).iterator().hasNext());
		ia = new int[] {};
		assertTrue(ContextNode.isFalsey(ia));
		assertFalse(ContextNode.ofRoot(ia).iterator().hasNext());
	}

	@SuppressWarnings("null")
	@Test
	public void testIteratorOnArray() throws Exception {
		int[] ia = new int[] { 1, 0 };
		var node = ContextNode.ofRoot(ia);
		StringBuilder sb = new StringBuilder();
		for (var n : node) {
			sb.append(n.renderString()).append("\n");
		}
		String expected = """
				1
				0
				""";
		assertEquals(expected, sb.toString());

	}

	@Test
	public void testEmptyMapIsNotFalsey() throws Exception {
		assertFalse(ContextNode.isFalsey(Map.of()));
	}

}
