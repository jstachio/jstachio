package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "inheritance/Overriddenparent.mustache")
@TemplateMapping({
@Template(name="parent", template="{{$stuff}}...{{/stuff}}"),
})
public class Overriddenparent extends SpecModel {
}
