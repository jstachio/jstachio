package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.GenerateRenderer;
import io.jstach.annotation.Template;
import io.jstach.annotation.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderer(template = "inheritance/Inherit.mustache")
@TemplateMapping({
@Template(name="include", template="{{$foo}}default content{{/foo}}"),
})
public class Inherit extends SpecModel {
}
