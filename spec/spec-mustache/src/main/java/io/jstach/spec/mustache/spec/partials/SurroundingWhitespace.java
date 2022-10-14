package io.jstach.spec.mustache.spec.partials;

import io.jstach.annotation.GenerateRenderableAdapter;
import io.jstach.annotation.Template;
import io.jstach.annotation.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "partials/SurroundingWhitespace.mustache")
@TemplateMapping({
@Template(name="partial", template="\t|\t"),
})
public class SurroundingWhitespace extends SpecModel {
}
