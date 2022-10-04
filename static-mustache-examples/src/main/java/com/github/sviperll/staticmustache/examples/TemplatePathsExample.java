package com.github.sviperll.staticmustache.examples;

import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.Template;
import com.github.sviperll.staticmustache.TemplateMapping;

@GenerateRenderableAdapter(template = "template-paths-example.mustache")
@TemplateMapping(@Template(name = "formerly-known-as-partial-include", path = "partial-include.mustache"))
public record TemplatePathsExample(String name) {

}
