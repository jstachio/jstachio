package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.GenerateRenderer;
import io.jstach.annotation.Template;
import io.jstach.annotation.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderer(template = "inheritance/Inheritindentation.mustache")
@TemplateMapping({
@Template(name="parent", template="stop:\n  {{$nineties}}collaborate and listen{{/nineties}}\n"),
})
public class Inheritindentation extends SpecModel {
}
