package io.jstach.examples;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartial;
import io.jstach.jstache.JStachePartials;

@JStache(path = "template-paths-example.mustache")
@JStachePartials(@JStachePartial(name = "formerly-known-as-partial-include", template = """
		I AM INLINED"""))
record InlineNamedTemplateExample(String name) {

}
