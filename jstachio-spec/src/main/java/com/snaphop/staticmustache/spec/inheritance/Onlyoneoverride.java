package com.snaphop.staticmustache.spec.inheritance;

import com.snaphop.staticmustache.spec.SpecModel;
import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.TemplateMapping;
import com.github.sviperll.staticmustache.Template;

@GenerateRenderableAdapter(template = "inheritance/Onlyoneoverride.mustache")
@TemplateMapping({
@Template(name="parent", template="{{$stuff}}new default one{{/stuff}}, {{$stuff2}}new default two{{/stuff2}}"),
})
public class Onlyoneoverride extends SpecModel {
}
