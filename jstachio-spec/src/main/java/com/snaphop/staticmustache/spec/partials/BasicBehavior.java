package com.snaphop.staticmustache.spec.partials;

import com.snaphop.staticmustache.spec.SpecModel;
import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.TemplateMapping;
import com.github.sviperll.staticmustache.Template;

@GenerateRenderableAdapter(template = "partials/BasicBehavior.mustache")
@TemplateMapping({
@Template(name="text", template="from partial"),
})
public class BasicBehavior extends SpecModel {
}
