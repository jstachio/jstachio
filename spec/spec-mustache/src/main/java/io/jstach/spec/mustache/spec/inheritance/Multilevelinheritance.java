package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.GenerateRenderer;
import io.jstach.annotation.Template;
import io.jstach.annotation.TemplateCompilerFlags;
import io.jstach.annotation.TemplateMapping;
import io.jstach.annotation.TemplateCompilerFlags.Flag;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderer(template = "inheritance/Multilevelinheritance.mustache")
@TemplateMapping({
@Template(name="parent", template="{{<older}}{{$a}}p{{/a}}{{/older}}"),
@Template(name="older", template="{{<grandParent}}{{$a}}o{{/a}}{{/grandParent}}"),
@Template(name="grandParent", template="{{$a}}g{{/a}}"),
})
@TemplateCompilerFlags(flags= {Flag.DEBUG })
public class Multilevelinheritance extends SpecModel {
}
