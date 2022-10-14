package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.GenerateRenderer;
import io.jstach.annotation.Template;
import io.jstach.annotation.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderer(template = "inheritance/Onlyoneoverride.mustache")
@TemplateMapping({
@Template(name="parent", template="{{$stuff}}new default one{{/stuff}}, {{$stuff2}}new default two{{/stuff2}}"),
})
public class Onlyoneoverride extends SpecModel {
}
