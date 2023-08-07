package io.jstach.examples;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import io.jstach.jstache.JStache;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Output;
import io.jstach.jstachio.context.ContextJStachio;
import io.jstach.jstachio.context.ContextNode;

public class ContextAwareTest {

	@Test
	public void testContextAwareOutput() throws Exception {
		Map<String, String> attributes = new LinkedHashMap<>();
		attributes.put("csrf", "TOKEN");
		var context = ContextNode.of(attributes::get);
		var model = new ContextAwareExample("Hello", IdContainer.test());
		var jstachio = ContextJStachio.of(JStachio.of());
		String actual = jstachio.execute(model, context, Output.of(new StringBuilder())).toString();
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
		var model = new ContextAwareExample("Hello", IdContainer.test());
		var jstachio = ContextJStachio.of(JStachio.of());
		String actual = jstachio.execute(model, Output.of(new StringBuilder())).toString();
		String expected = """

				Hello
				098f6bcd-4621-3373-8ade-4e832627b4f6
				""";
		assertEquals(expected, actual);
	}

	@Test
	public void testName() throws Exception {
		String actual = JStachio.render(new OtherContextVariables("Adam"));
		String expected = """
				io.jstach.examples.OtherContextVariablesRenderer
				Adam
				Adam
				""";
		assertEquals(expected, actual);
	}

	@JStache(template = """
			{{@template.templateName}}
			{{@root.name}}
			{{#@template}}
			{{@root.name}}
			{{/@template}}
			""")
	record OtherContextVariables(String name) {

	}

}
