package io.jstach.examples.using;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MyConfigModelTest {

	@Test
	public void test() {
		MyConfigModelPack template = MyConfigModelPack.of();
		/*
		 * Not much to assert since the compiler is doing most of the work
		 */
		assertTrue(template instanceof MyConfig.MyConfigInterface);
	}

}
