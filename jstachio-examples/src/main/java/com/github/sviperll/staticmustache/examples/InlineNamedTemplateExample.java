package com.github.sviperll.staticmustache.examples;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;

@GenerateRenderableAdapter(template = "template-paths-example.mustache")
@TemplateMapping(@Template(name = "formerly-known-as-partial-include", 
    template = """
            I AM INLINED"""))
public record InlineNamedTemplateExample(String name) {

}
