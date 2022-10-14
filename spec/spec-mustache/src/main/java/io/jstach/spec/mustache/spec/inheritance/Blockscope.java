package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "inheritance/Blockscope.mustache")
@TemplateMapping({
@Template(name="parent", template="{{#nested}}{{$block}}You say {{fruit}}.{{/block}}{{/nested}}"),
})
public class Blockscope extends SpecModel {
}
