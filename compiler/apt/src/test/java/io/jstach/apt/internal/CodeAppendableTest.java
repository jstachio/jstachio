package io.jstach.apt.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CodeAppendableTest {

	@Test
	public void testIssue18() {
		String template = """
				bugged "here"
				""";

		assertEquals("bugged \"here\"\n", template);

		String actual = CodeAppendable.stringConcat(template);
		String expected = "\n" + "    \"bugged \\\"here\\\"\\n\"";

		assertEquals(expected, actual);

	}

}
