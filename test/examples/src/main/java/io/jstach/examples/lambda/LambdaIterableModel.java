package io.jstach.examples.lambda;

import java.util.List;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheFlags.Flag;
import io.jstach.jstache.JStacheLambda;

@JStache(template = """
		{{#names}}
		{{#.}}
		{{message}} {{.}}!
		{{/.}}
		{{/names}}
		""")
@JStacheFlags(flags = Flag.DEBUG)
public record LambdaIterableModel(String message) {

	@JStacheLambda
	public List<String> names(Object o) {
		return List.of("Cartman", "Eric", "Kyle", "Stan", "Kenny");
	}
}
