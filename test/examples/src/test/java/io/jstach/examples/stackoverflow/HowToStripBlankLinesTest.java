package io.jstach.examples.stackoverflow;

import static org.junit.Assert.*;

import org.junit.Test;

import io.jstach.jstachio.JStachio;

public class HowToStripBlankLinesTest {

	@Test
	public void test() {
		HowToStripBlankLines m = new HowToStripBlankLines("How", null, "Yes", null, "Nice");
		String actual = JStachio.render(m);
		String expected = """
				How
				Yes
				Nice
				""";

		assertEquals(expected, actual);
	}

}
