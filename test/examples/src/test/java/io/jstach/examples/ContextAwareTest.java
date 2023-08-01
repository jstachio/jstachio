package io.jstach.examples;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Output;
import io.jstach.jstachio.context.ContextNode;
import io.jstach.jstachio.output.ContextAwareOutput;

public class ContextAwareTest {

	@Test
	public void testContextAwareOutput() throws Exception {
		var output = Output.of(new StringBuilder());
		Map<String, String> attributes = new LinkedHashMap<>();
		attributes.put("csrf", "TOKEN");
		var context = ContextNode.of(attributes::get);
		var contextOutput = ContextAwareOutput.of(output, context);
		var model = new ContextAwareExample("Hello", IdContainer.test());
		String actual = JStachio.of().execute(model, contextOutput).getOutput().toString();
		String expected = """
				TOKEN
				Hello
				098f6bcd-4621-3373-8ade-4e832627b4f6
				From myLambda TOKEN
				""";
		assertEquals(expected, actual);
	}

	@Test
	public void testContextAwareMissing() throws Exception {
		var output = Output.of(new StringBuilder());
		var model = new ContextAwareExample("Hello", IdContainer.test());
		String actual = JStachio.of().execute(model, output).toString();
		String expected = """

				Hello
				098f6bcd-4621-3373-8ade-4e832627b4f6
				""";
		assertEquals(expected, actual);
	}

}
