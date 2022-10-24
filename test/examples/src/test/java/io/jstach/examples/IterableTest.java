package io.jstach.examples;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import io.jstach.JStachio;

public class IterableTest {

	@Test
	public void test() {
		String actual = JStachio.render(new IterableExample(List.of("Kenny", "Eric", "Stan"), true));
		String expected = """


				I am first Kenny
				    My one based index is 1
				    My zero based index is 0

				-------
				 Eric
				    My one based index is 2
				    My zero based index is 1

				-------
				I am last Stan
				    My one based index is 3
				    My zero based index is 2
				                """;

		assertEquals(expected, actual);
	}

	@Test
	public void testEmpty() throws Exception {
		String actual = JStachio.render(new IterableExample(List.of(), true));

		String expected = "no show\n";

		assertEquals(expected, actual);

	}

}
