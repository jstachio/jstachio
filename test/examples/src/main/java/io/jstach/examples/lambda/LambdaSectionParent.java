package io.jstach.examples.lambda;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheLambda;

@JStache(template = """
		{{#trans}}
		{{$bingo}}bingo{{/bingo}}
		{{$message}}Hello{{/message}}{{/trans}}
		""")
public record LambdaSectionParent(String stuff) {

	public record Translation(String tx, boolean found) {
	}

	@JStacheLambda(template = """
			{{<@section}}
			{{$message}}{{tx}}{{/message}}
			asdfasf
			{{/@section}}""")
	public Translation trans(Object o) {
		return new Translation(o.toString(), false);
	}
}
