package io.jstach.examples;

import io.jstach.jstache.JStache;

@JStache(template = """
		{{@context.csrf}}
		{{#@context.user}}
		{{.}}
		{{/@context.user}}
		{{message}}
		""")
public record ContextAwareExample(String message) {

}
