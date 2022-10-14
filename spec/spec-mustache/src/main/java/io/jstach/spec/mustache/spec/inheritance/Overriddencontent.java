package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.GenerateRenderableAdapter;
import io.jstach.annotation.Template;
import io.jstach.annotation.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "inheritance/Overriddencontent.mustache")
@TemplateMapping({
@Template(name="super", template="...{{$title}}Default title{{/title}}..."),
})
public class Overriddencontent extends SpecModel {
}
