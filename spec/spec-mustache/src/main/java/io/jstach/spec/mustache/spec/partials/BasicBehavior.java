package io.jstach.spec.mustache.spec.partials;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "partials/BasicBehavior.mustache")
@TemplateMapping({
@Template(name="text", template="from partial"),
})
public class BasicBehavior extends SpecModel {
}
