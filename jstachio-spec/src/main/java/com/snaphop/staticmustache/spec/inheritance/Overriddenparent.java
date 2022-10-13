package com.snaphop.staticmustache.spec.inheritance;

import com.snaphop.staticmustache.spec.SpecModel;
import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.TemplateMapping;
import com.github.sviperll.staticmustache.Template;

@GenerateRenderableAdapter(template = "inheritance/Overriddenparent.mustache")
@TemplateMapping({
@Template(name="parent", template="{{$stuff}}...{{/stuff}}"),
})
public class Overriddenparent extends SpecModel {
}
