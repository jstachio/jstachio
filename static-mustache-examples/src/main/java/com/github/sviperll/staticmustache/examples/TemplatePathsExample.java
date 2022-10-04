package com.github.sviperll.staticmustache.examples;

import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.TemplatePaths;
import com.github.sviperll.staticmustache.TemplatePaths.TemplatePath;

@GenerateRenderableAdapter(template="template-paths-example.mustache")
@TemplatePaths(
@TemplatePath(name="formerly-known-as-partial-include", path="partial-include.mustache"))
public record TemplatePathsExample(String name) {

}
