package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;
import io.jstach.spec.generator.SpecModel;

@GenerateRenderableAdapter(template = "inheritance/Onlyoneoverride.mustache")
@TemplateMapping({
@Template(name="parent", template="{{$stuff}}new default one{{/stuff}}, {{$stuff2}}new default two{{/stuff2}}"),
})
public class Onlyoneoverride extends SpecModel {
}
