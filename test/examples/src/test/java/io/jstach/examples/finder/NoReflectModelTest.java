package io.jstach.examples.finder;

import org.junit.Test;

import io.jstach.jstachio.JStachio;

public class NoReflectModelTest {

	@Test
	public void testName() throws Exception {
		JStachio.render(new NoReflectModel("asdfasdf"));
	}

}
