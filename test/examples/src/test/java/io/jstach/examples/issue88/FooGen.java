package io.jstach.examples.issue88;

import java.util.List;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheLambda;

@JStache(template = """
		{{#options}}
		{{#transform}}
		{{name}}
		{{/transform}}
		{{/options}}""")
public record FooGen(List<GenericRecord<?>> options) {
	record GenericRecord<T> (T value) {
	}

	@JStacheLambda
	public CRecord transform(GenericRecord<?> option) {
		return new CRecord("repro");
	}

	record CRecord(String name) {
	}
}
