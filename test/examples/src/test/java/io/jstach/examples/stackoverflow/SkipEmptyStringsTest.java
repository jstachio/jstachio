package io.jstach.examples.stackoverflow;

import static org.junit.Assert.*;

import org.junit.Test;

import io.jstach.jstachio.JStachio;

// https://stackoverflow.com/questions/74746407/how-to-skip-empty-string-with-mustache
public class SkipEmptyStringsTest {

	@Test
	public void testRomeo() {
		SkipEmptyStrings e = new SkipEmptyStrings("Alpha", "Romeo", "Julia");
		String actual = JStachio.render(e);
		String expected = """
				Alpha
				Romeo


				Julia
				""";
		assertEquals(expected, actual);
	}

	@Test
	public void testRomeoNull() {
		SkipEmptyStrings e = new SkipEmptyStrings("Alpha", null, "Julia");
		String actual = JStachio.render(e);
		String expected = """
				Alpha
				Julia
				""";
		assertEquals(expected, actual);
	}

	@Test
	public void testRomeoEmpty() {
		SkipEmptyStrings e = new SkipEmptyStrings("Alpha", "", "Julia");
		String actual = JStachio.render(e);
		String expected = """
				Alpha
				Julia
				""";
		assertEquals(expected, actual);
	}

}
