package io.jstach.examples;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartials;

@JStache(path = "template-paths-example.mustache")
@JStachePartials(@JStachePartial(name = "formerly-known-as-partial-include", template = """
		I AM INLINED"""))
public record InlineNamedTemplateExample(String name) {

}
