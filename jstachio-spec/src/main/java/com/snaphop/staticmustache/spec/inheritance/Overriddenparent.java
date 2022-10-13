package com.snaphop.staticmustache.spec.inheritance;

import com.snaphop.staticmustache.spec.SpecModel;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;

@GenerateRenderableAdapter(template = "inheritance/Overriddenparent.mustache")
@TemplateMapping({
@Template(name="parent", template="{{$stuff}}...{{/stuff}}"),
})
public class Overriddenparent extends SpecModel {
}
