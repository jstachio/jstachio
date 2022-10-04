package com.snaphop.staticmustache.spec.partials;

import com.snaphop.staticmustache.spec.SpecModel;
import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.TemplateMapping;
import com.github.sviperll.staticmustache.Template;

@GenerateRenderableAdapter(template = "partials/StandaloneIndentation.mustache")
@TemplateMapping({
@Template(name="partial", template="|\n{{{content}}}\n|\n"),
})
public class StandaloneIndentation extends SpecModel {
}
