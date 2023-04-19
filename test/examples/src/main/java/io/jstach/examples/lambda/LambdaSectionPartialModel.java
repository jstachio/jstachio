package io.jstach.examples.lambda;

import java.util.List;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheLambda;

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
			to defeat Darth Sideous.
			{{/-last}}
			{{/list}}
			""")
	public LambdaModel lambda(Object o) {
		return new LambdaModel(List.of(new Model("Luke"), new Model("Leia")));
	}
}
