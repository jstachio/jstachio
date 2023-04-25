package io.jstach.examples.lambda;

import java.util.List;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheLambda;

@JStache(template = """
		{{#names}}
		{{#.}}
		{{message}} {{.}}!
		{{/.}}
		{{/names}}
		""")
public record LambdaIterable(String message) {

	@JStacheLambda
	public List<String> names(Object o) {
		return List.of("Eric", "Kyle");
	}
}
