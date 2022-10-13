package com.snaphop.staticmustache.spec.inheritance;

import com.snaphop.staticmustache.spec.SpecModel;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;

@GenerateRenderableAdapter(template = "inheritance/Inherit.mustache")
@TemplateMapping({
@Template(name="include", template="{{$foo}}default content{{/foo}}"),
})
public class Inherit extends SpecModel {
}
