package com.snaphop.staticmustache.spec;

import static com.snaphop.staticmustache.spec.AbstractSpecTest.testSpec;

import org.junit.Test;

import io.jstach.spec.mustache.spec.custom.DottedNamesTruthyRenderer;
import io.jstach.spec.mustache.spec.custom.Custom.DottedNamesTruthy;
import io.jstach.spec.mustache.spec.inverted.InvertedSpecTemplate;

public class CustomTest {

	@Test
	public void testDottedNamesTruthy() {
		DottedNamesTruthy t = DottedNamesTruthy.test();
		String actual = DottedNamesTruthyRenderer.of(t).renderString();
		testSpec(InvertedSpecTemplate.DOTTED_NAMES___TRUTHY, actual);
	}

	// @Test
	// public void testDottedNamesBrokenChainResolution() {
	// DottedNamesBrokenChainResolution t = DottedNamesBrokenChainResolution.test();
	// String actual = DottedNamesBrokenChainResolutionRenderer.of(t).renderString();
	// testSpec(InterpolationSpecTemplate.DOTTED_NAMES___BROKEN_CHAIN_RESOLUTION, actual);
	// }

}
