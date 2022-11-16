package io.jstach.examples;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartial;
import io.jstach.jstache.JStachePartials;

@JStache(path = "template-paths-example.mustache")
@JStachePartials(@JStachePartial(name = "formerly-known-as-partial-include", path = "partial-include.mustache"))
record TemplatePathsExample(String name) {

}
