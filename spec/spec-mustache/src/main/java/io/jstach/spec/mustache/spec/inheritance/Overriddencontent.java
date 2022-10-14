package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "inheritance/Overriddencontent.mustache")
@TemplateMapping({
@Template(name="super", template="...{{$title}}Default title{{/title}}..."),
})
public class Overriddencontent extends SpecModel {
}
