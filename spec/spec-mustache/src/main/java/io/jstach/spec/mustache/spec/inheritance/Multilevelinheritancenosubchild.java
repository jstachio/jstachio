package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.GenerateRenderableAdapter;
import io.jstach.annotation.Template;
import io.jstach.annotation.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "inheritance/Multilevelinheritancenosubchild.mustache")
@TemplateMapping({
@Template(name="parent", template="{{<older}}{{$a}}p{{/a}}{{/older}}"),
@Template(name="older", template="{{<grandParent}}{{$a}}o{{/a}}{{/grandParent}}"),
@Template(name="grandParent", template="{{$a}}g{{/a}}"),
})
public class Multilevelinheritancenosubchild extends SpecModel {
}
