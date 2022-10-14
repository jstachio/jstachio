package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.GenerateRenderer;
import io.jstach.annotation.Template;
import io.jstach.annotation.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderer(template = "inheritance/Blockscope.mustache")
@TemplateMapping({
@Template(name="parent", template="{{#nested}}{{$block}}You say {{fruit}}.{{/block}}{{/nested}}"),
})
public class Blockscope extends SpecModel {
}
