package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "inheritance/Inheritindentation.mustache")
@TemplateMapping({
@Template(name="parent", template="stop:\n  {{$nineties}}collaborate and listen{{/nineties}}\n"),
})
public class Inheritindentation extends SpecModel {
}
