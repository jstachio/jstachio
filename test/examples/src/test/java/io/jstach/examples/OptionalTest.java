package io.jstach.examples;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import io.jstach.JStachio;

public class OptionalTest {

	@Test
	public void testName() throws Exception {
		OptionalContainer child = new OptionalContainer(Optional.empty(), true, Optional.empty());

		OptionalContainer oc = new OptionalContainer(Optional.of("blah"), false, Optional.of(child));

		String expected = """
				false

				<name>
				blah
				</name>

				<inverted-name>
				</inverted-name>

				<name>
				blah
				</name>

				<child.name>
				</child.name>

				<child.myBoolean>
				true -> true
				</child.myBoolean>

				<child.myBoolean>
				true -> true
				</child.myBoolean>

				<inverted-child.name>
				no show child name
				</inverted-child.name>""";
		String actual = JStachio.render(oc);

		assertEquals(expected, actual);
	}

}
