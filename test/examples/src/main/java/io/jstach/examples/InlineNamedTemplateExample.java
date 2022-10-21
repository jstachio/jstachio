package io.jstach.examples;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartialMapping;

@JStache(path = "template-paths-example.mustache")
@JStachePartialMapping(@JStachePartial(name = "formerly-known-as-partial-include", template = """
		I AM INLINED"""))
public record InlineNamedTemplateExample(String name) {

}
