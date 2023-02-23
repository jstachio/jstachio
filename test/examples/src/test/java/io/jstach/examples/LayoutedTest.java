package io.jstach.examples;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.jstach.jstachio.JStachio;

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

	@Test
	public void testLayoutMessage() {
		LayoutedNestedExample example = new LayoutedNestedExample("stuff", new Data("Hello World!"));

		var actual = JStachio.render(example);
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
