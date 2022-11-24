package io.jstach.examples;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LayoutedTest {

	@Test
	public void testLayout() {
		LayoutedExample example = new LayoutedExample("stuff", "Hello World!");

		var actual = LayoutedExampleRenderer.of().execute(example);
		String expected = """
				<!doctype html>
				<html>
					<head>
						<meta charset="UTF-8">
						<title>stuff</title>
					</head>
					<body>
						<span>Hello World!</span>
					</body>
				</html>
						""";
		assertEquals(expected.replaceAll("\t", "  "), actual.replaceAll("\t", "  "));
	}

}
