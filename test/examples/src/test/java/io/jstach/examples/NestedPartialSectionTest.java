package io.jstach.examples;

import static org.junit.Assert.*;

import org.junit.Test;

public class NestedPartialSectionTest {

	@Test
	public void test() {
		String result = NestedPartialSectionRenderer.of().execute(new NestedPartialSection("value"));
		String expected = """
				\\
				 |
				 value
				 |
				/
				""";
		assertEquals(expected, result);
	}

}
