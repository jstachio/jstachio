package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "inheritance/Overrideparentwithnewlines.mustache")
@TemplateMapping({
@Template(name="parent", template="{{$ballmer}}peaking{{/ballmer}}"),
})
public class Overrideparentwithnewlines extends SpecModel {
}
