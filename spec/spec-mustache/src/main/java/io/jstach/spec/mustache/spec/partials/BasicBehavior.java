package io.jstach.spec.mustache.spec.partials;

import io.jstach.annotation.GenerateRenderableAdapter;
import io.jstach.annotation.Template;
import io.jstach.annotation.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "partials/BasicBehavior.mustache")
@TemplateMapping({
@Template(name="text", template="from partial"),
})
public class BasicBehavior extends SpecModel {
}
