package io.jstach.spec.mustache.spec.partials;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "partials/SurroundingWhitespace.mustache")
@TemplateMapping({
@Template(name="partial", template="\t|\t"),
})
public class SurroundingWhitespace extends SpecModel {
}
