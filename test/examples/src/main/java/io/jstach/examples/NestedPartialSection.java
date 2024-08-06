package io.jstach.examples;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartial;
import io.jstach.jstache.JStachePartials;

@JStache(template = """
		\\
		 {{>partial}}
		/
		""")
@JStachePartials({ @JStachePartial(name = "partial", template = """
		|
		{{#content}}
		{{#content}}
		{{.}}
		{{/content}}
		{{/content}}
		|
		""") })
public record NestedPartialSection(String content) {
}
