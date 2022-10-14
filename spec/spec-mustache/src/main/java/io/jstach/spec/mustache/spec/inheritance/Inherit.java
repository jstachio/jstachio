package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "inheritance/Inherit.mustache")
@TemplateMapping({
@Template(name="include", template="{{$foo}}default content{{/foo}}"),
})
public class Inherit extends SpecModel {
}
