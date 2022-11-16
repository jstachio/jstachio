package io.jstach.examples;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.Test;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheLambda;
import io.jstach.jstachio.JStachio;

public class HelloWorldTest {

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

	}

}
