package io.jstach.spec.mustache.spec.partials;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "partials/StandaloneLineEndings.mustache")
@TemplateMapping({
@Template(name="partial", template=">"),
})
public class StandaloneLineEndings extends SpecModel {
}
