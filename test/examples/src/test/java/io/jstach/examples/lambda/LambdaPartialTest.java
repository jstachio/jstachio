package io.jstach.examples.lambda;

import static org.junit.Assert.*;

import org.junit.Test;

import io.jstach.jstachio.JStachio;

public class LambdaPartialTest {

	@Test
	public void testPartial() throws Exception {
		LambdaSectionPartialModel m = new LambdaSectionPartialModel("Hello");
		String actual = JStachio.render(m);
		String expected = """
				Use the force Luke!
				Use the force Leia!
				to defeat Darth Sideous.
								""";
		assertEquals(expected, actual);
	}

	@Test
	public void testParent() throws Exception {
		LambdaSectionParent m = new LambdaSectionParent("ignore");
		String actual = JStachio.render(m);
		String expected = """
				bingo
				LambdaSectionParent[stuff&#x3D;ignore]
								""";
		assertEquals(expected, actual);
	}

}
