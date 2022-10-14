package io.jstach.spec.mustache.spec.partials;

import io.jstach.annotation.GenerateRenderableAdapter;
import io.jstach.annotation.Template;
import io.jstach.annotation.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "partials/StandaloneWithoutNewline.mustache")
@TemplateMapping({
@Template(name="partial", template=">\n>"),
})
public class StandaloneWithoutNewline extends SpecModel {
}
