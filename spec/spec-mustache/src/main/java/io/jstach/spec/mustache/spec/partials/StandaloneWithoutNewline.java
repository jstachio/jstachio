package io.jstach.spec.mustache.spec.partials;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "partials/StandaloneWithoutNewline.mustache")
@TemplateMapping({
@Template(name="partial", template=">\n>"),
})
public class StandaloneWithoutNewline extends SpecModel {
}
