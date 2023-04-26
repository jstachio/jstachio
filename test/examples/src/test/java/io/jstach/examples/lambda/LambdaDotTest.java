package io.jstach.examples.lambda;

import static org.junit.Assert.*;

import org.junit.Test;

import io.jstach.jstachio.JStachio;

public class LambdaDotTest {

	@Test
	public void test() {
		LambdaIterableModel m = new LambdaIterableModel("Hello");
		String actual = JStachio.render(m);
		String expected = """
				Hello Cartman!
				Hello Eric!
				Hello Kyle!
				Hello Stan!
				Hello Kenny!
				""";
		assertEquals(expected, actual);
	}

}
