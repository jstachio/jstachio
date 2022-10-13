package com.github.sviperll.staticmustache.examples;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;

@GenerateRenderableAdapter(template = "template-paths-example.mustache")
@TemplateMapping(@Template(name = "formerly-known-as-partial-include", path = "partial-include.mustache"))
public record TemplatePathsExample(String name) {

}
