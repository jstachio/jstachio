package io.jstach.examples;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheFlags.Flag;
import io.jstach.jstache.JStacheLambda;
import io.jstach.jstache.JStachePartial;
import io.jstach.jstache.JStachePartials;
import io.jstach.jstachio.JStachio;

public class LambdaTest {

	@Test
	public void testLambda() {
		// Funny story... Map.of does not consistent ordering
		// Map.of("Color", "Red", "Food", "Pizza", "Speed", "Fast")
		Map<String, String> m = new LinkedHashMap<>();
		m.put("Color", "Red");
		m.put("Food", "Pizza");
		m.put("Speed", "Fast");
		LambdaExample example = new LambdaExample("stuff", m);

		var actual = LambdaExampleRenderer.of().execute(example);
		// We do not interpolate the results so the below is expected
		String expected = """
				<hello>Adam {{name}} </hello>: stuff

				Color : Red
				Food : Pizza
				Speed : Fast
				Key: Color, Value: Red
				Key: Food, Value: Pizza
				Key: Speed, Value: Fast
				""";
		assertEquals(expected, actual);
	}

	/*
	 * { "items" : [ 1, 2, 3], "lambda" : function(input, item) {...no analog in current
	 * spec..} }
	 */
	static final String template = """
			{{#items}}{{#lambda}}{{item}} is {{stripe}}{{/lambda}}{{/items}}""";

	@JStache(template = template)
	public record Items(List<Integer> items) {
		@JStacheLambda
		public LambdaModel lambda(Integer item) {
			return new LambdaModel(item, item % 2 == 0 ? "even" : "odd");
		}
	}

	/*
	 * In jstachio if you return an object it is then pushed on the stack and the contents
	 * of the of the lambda block are used as a sort of inline partial.
	 *
	 * This is in large part because we cannot handle dynamic templates and also because I
	 * think it is the correct usage is as the caller knows how it wants to render things
	 * and avoids the whole delimeter nightmare.
	 */
	public record LambdaModel(Integer item, String stripe) {
	}

	@Test
	public void testName() throws Exception {
		String expected = "5 is odd";
		String actual = JStachio.render(new Items(List.of(5)));
		assertEquals(expected, actual);
	}

	public record Person(String name) {
	}

	@JStache(template = """
			{{<parent}}
				{{$block}}sprinklers{{/block}}
			{{/parent}}""")
	@JStachePartials(@JStachePartial(name = "parent", template = """
			{{#lambda}}
			Use the {{$block}}force{{/block}}, {{name}}.
			{{/lambda}}"""))
	public record PersonPage(String name) {

		@JStacheLambda
		public Person lambda(Object ignore) {
			return new Person("darling");
		}
	}

	@Test
	public void testParentLambda() throws Exception {
		String expected = """
				Use the sprinklers, darling.
				""";
		String actual = JStachio.render(new PersonPage("Luke"));

		assertEquals(expected, actual);
	}

	@JStache(template = """
			{{#decorate}}
			{{lastName}}. {{name}}
			{{/decorate}}
			{{#decorate}}
			{{lastName}}. {{name}}
			{{/decorate}}
			""")
	@JStacheFlags(flags = Flag.DEBUG)
	record FullContext(String name) {

		@JStacheLambda
		public Decorated decorate(FullContext ctx) {
			String[] s = ctx.name().split(" ", 2);
			String firstName = s[0];
			String lastName = s.length > 1 ? s[1] : "";
			return new Decorated(firstName, lastName);
		}
	}

	record Decorated(String firstName, String lastName) {

	}

	@Test
	public void testFullContext() {
		String expected = """
				Bond. James Bond
				Bond. James Bond
				""";
		String actual = JStachio.render(new FullContext("James Bond"));

		assertEquals(expected, actual);
	}

	@JStache(template = """
			{{#length}}
			123
			{{/length}}
			""")
	@JStacheFlags(flags = Flag.DEBUG)
	record SectionLength(String name) {

		@JStacheLambda
		@JStacheLambda.Raw
		public String length(@JStacheLambda.Raw String section) {
			return "" + section.length();
		}
	}

	@Test
	public void testSectionLength() {
		String expected = "4";
		String actual = JStachio.render(new SectionLength("12345"));
		assertEquals(expected, actual);
	}

}
