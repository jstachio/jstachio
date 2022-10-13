package com.snaphop.staticmustache.spec.partials;

import com.snaphop.staticmustache.spec.SpecModel;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;

@GenerateRenderableAdapter(template = "partials/BasicBehavior.mustache")
@TemplateMapping({
@Template(name="text", template="from partial"),
})
public class BasicBehavior extends SpecModel {
}
