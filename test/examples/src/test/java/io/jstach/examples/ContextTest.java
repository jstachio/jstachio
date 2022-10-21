package io.jstach.examples;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ContextTest {

	@Test
	public void testParent() throws Exception {
		ContextExample ce = ContextExample.forTest();
		String r = ContextExampleRenderer.of(ce).renderString();
		System.out.println(r);

		String expected = "\"foo, bar, baz\"";

		assertEquals(expected, r);
	}

}
