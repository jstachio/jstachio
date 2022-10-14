package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.GenerateRenderableAdapter;
import io.jstach.annotation.Template;
import io.jstach.annotation.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "inheritance/Textinsideparent.mustache")
@TemplateMapping({
@Template(name="parent", template="{{$foo}}default content{{/foo}}"),
})
public class Textinsideparent extends SpecModel {
}
