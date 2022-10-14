package com.github.sviperll.staticmustache.examples;

import io.jstach.annotation.GenerateRenderableAdapter;
import io.jstach.annotation.Template;
import io.jstach.annotation.TemplateMapping;

@GenerateRenderableAdapter(template = "template-paths-example.mustache")
@TemplateMapping(@Template(name = "formerly-known-as-partial-include", 
    template = """
            I AM INLINED"""))
public record InlineNamedTemplateExample(String name) {

}
