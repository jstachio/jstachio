package com.snaphop.staticmustache.spec.partials;

import com.snaphop.staticmustache.spec.SpecModel;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;

@GenerateRenderableAdapter(template = "partials/InlineIndentation.mustache")
@TemplateMapping({
@Template(name="partial", template=">\n>"),
})
public class InlineIndentation extends SpecModel {
}
