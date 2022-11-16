package io.jstach.examples;

import static org.junit.Assert.*;

import org.junit.Test;

import io.jstach.examples.EnumExample.Sign;
import io.jstach.jstachio.JStachio;

public class EnumTest {

	@Test
	public void testEnumExample() throws Exception {
		EnumExample example = new EnumExample("Hello", Sign.RED_LIGHT);

		String expected = """
				Hello
				STOP!!!!
				""";
		String actual = JStachio.render(example);

		assertEquals(expected, actual);

	}

	@Test
	public void testEnumInverted() throws Exception {
		EnumExample example = new EnumExample("Hello", Sign.GREEN_LIGHT);

		String expected = """
				Hello
				Go go!""";
		String actual = JStachio.render(example);

		assertEquals(expected, actual);

	}

}
