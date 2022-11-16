package io.jstach.examples;

import java.util.Map;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheLambda;
import io.jstach.jstache.JStacheFlags.Flag;
import io.jstach.jstache.JStacheLambda.Raw;

@JStache(path = "lambda-example.mustache")
@JStacheFlags(flags = { Flag.DEBUG })
record LambdaExample(String name, Map<String, String> props) implements Lambdas {

	@JStacheLambda
	public @Raw String hello(@Raw String html, String name) {
		return "<hello>" + html + "</hello>: " + name;
	}
}
