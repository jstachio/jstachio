package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.GenerateRenderer;
import io.jstach.annotation.Template;
import io.jstach.annotation.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderer(template = "inheritance/Overriddencontent.mustache")
@TemplateMapping({
@Template(name="super", template="...{{$title}}Default title{{/title}}..."),
})
public class Overriddencontent extends SpecModel {
}
