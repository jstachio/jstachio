package io.jstach.examples.delimiter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DelimiterExampleTest {

	@Test
	public void test() {
		DelimiterExample e = new DelimiterExample("hello");
		String actual = DelimiterExampleRenderer.of().execute(e);
		String expected = """
				hello
				hello
				""";
		assertEquals(expected, actual);
	}

}
