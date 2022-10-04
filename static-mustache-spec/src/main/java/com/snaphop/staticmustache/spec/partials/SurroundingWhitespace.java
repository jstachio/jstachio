package com.snaphop.staticmustache.spec.partials;

import com.snaphop.staticmustache.spec.SpecModel;
import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.TemplateMapping;
import com.github.sviperll.staticmustache.Template;

@GenerateRenderableAdapter(template = "partials/SurroundingWhitespace.mustache")
@TemplateMapping({
@Template(name="partial", template="\t|\t"),
})
public class SurroundingWhitespace extends SpecModel {
}
