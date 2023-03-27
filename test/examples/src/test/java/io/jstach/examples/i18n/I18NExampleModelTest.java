package io.jstach.examples.i18n;

import static org.junit.Assert.*;

import org.junit.Test;

import io.jstach.jstachio.JStachio;

public class I18NExampleModelTest {

	@Test
	public void test() {
		I18NExampleModel m = new I18NExampleModel("Adam");
		String actual = JStachio.render(m);
		String expected = """
				Hello Adam!
				""";
		assertEquals(expected, actual);
	}

}
