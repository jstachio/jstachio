package io.jstach.examples;

import io.jstach.annotation.GenerateRenderer;
import io.jstach.annotation.Template;
import io.jstach.annotation.TemplateMapping;

@GenerateRenderer(template = "template-paths-example.mustache")
@TemplateMapping(@Template(name = "formerly-known-as-partial-include", path = "partial-include.mustache"))
public record TemplatePathsExample(String name) {

}
