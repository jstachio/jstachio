package io.jstach.opt.jmustache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheLambda;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Output;
import io.jstach.jstachio.context.ContextJStachio;
import io.jstach.jstachio.context.ContextNode;
import io.jstach.jstachio.spi.JStachioExtensions;
import io.jstach.jstachio.spi.JStachioFactory;

public class JMustacheRendererTest {

	@JStache(template = """
			{{#people}}
			{{message}} {{name}}! You are {{#ageInfo}}{{age}}{{/ageInfo}} years old!
			{{#-last}}
			That is all for now!
			{{/-last}}
			{{/people}}
			""")
	public record HelloWorld(String message, List<Person> people) implements AgeLambdaSupport {
	}

	public record Person(String name, LocalDate birthday) {
	}

	public record AgeInfo(long age, String date) {
	}

	public interface AgeLambdaSupport {

		@JStacheLambda
		default AgeInfo ageInfo(Person person) {
			long age = ChronoUnit.YEARS.between(person.birthday(), LocalDate.now());
			String date = person.birthday().format(DateTimeFormatter.ISO_DATE);
			return new AgeInfo(age, date);
		}

	}

	@Test
	public void testPerson() throws Exception {
		Person rick = new Person("Rick", LocalDate.now().minusYears(70));
		Person morty = new Person("Morty", LocalDate.now().minusYears(14));
		Person beth = new Person("Beth", LocalDate.now().minusYears(35));
		Person jerry = new Person("Jerry", LocalDate.now().minusYears(35));
		String actual = JStachio.render(new HelloWorld("Hello alien", List.of(rick, morty, beth, jerry)));
		String expected = """
				Hello alien Rick! You are 70 years old!
				Hello alien Morty! You are 14 years old!
				Hello alien Beth! You are 35 years old!
				Hello alien Jerry! You are 35 years old!
				That is all for now!
				                """;
		assertEquals(expected, actual);
		String prefix = "JMUSTACHE ";
		jmustache().use(true).prefix(prefix);
		actual = JStachio.render(new HelloWorld("Hello alien", List.of(rick, morty, beth, jerry)));
		assertEquals(prefix + expected, actual);

	}

	@JStache(template = """
			{{#lambda}}
			Use the force {{name}}!
			{{/lambda}}
			""")
	public record LambdaSectionPartialModel(String message) {

		public record Model(String name) {
		}

		public record LambdaModel(List<Model> list) {
		}

		@JStacheLambda(template = """
				{{#list}}
				{{>@section}}
				{{#-last}}
				To defeat Darth Sideous.
				{{/-last}}
				{{/list}}
				""")
		public LambdaModel lambda(Object o) {
			return new LambdaModel(List.of(new Model("Luke"), new Model("Leia")));
		}
	}

	@Test
	public void testSectionPartial() throws Exception {
		LambdaSectionPartialModel m = new LambdaSectionPartialModel("hello");
		JMustacheRenderer jmustacheExt = jmustache();
		jmustacheExt.use(false);
		String expected = """
				Use the force Luke!
				Use the force Leia!
				To defeat Darth Sideous.
								""";
		String actual = JStachio.render(m);
		assertEquals(expected, actual);
		jmustacheExt.use(true);
		actual = JStachio.render(m);
		/*
		 * jmustache has a bug with sections in that they are not standalone
		 */
		actual = actual.replace("\n", "");
		expected = expected.replace("\n", "");
		assertEquals(expected, actual);
	}

	@JStache(template = """
			{{@context.message}}
			""")
	public record ContextModel(String message) {
	}

	@Test
	public void testContext() throws Exception {
		JMustacheRenderer jmustacheExt = jmustache();
		ContextModel m = new ContextModel("hello");
		String expected = """
				boo
				""";
		/*
		 * JMustache does not support context at the moment. This is just to check the
		 * context gets through the filters.
		 */
		jmustacheExt.use(false);
		Map<String, Object> context = Map.of("message", "boo");
		String actual = ContextJStachio.of(JStachio.of())
				.execute(m, ContextNode.of(context::get), Output.of(new StringBuilder())).toString();
		assertEquals(expected, actual);
	}

	private JMustacheRenderer jmustache() {
		JMustacheRenderer jmustacheExt;
		if (JStachioFactory.defaultJStachio() instanceof JStachioExtensions.Provider je) {
			jmustacheExt = je.extensions().findExtension(JMustacheRenderer.class).orElseThrow();
		}
		else {
			fail();
			throw new IllegalStateException();
		}
		return jmustacheExt;
	}

}
