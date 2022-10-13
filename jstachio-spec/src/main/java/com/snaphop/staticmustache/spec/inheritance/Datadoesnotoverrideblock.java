package com.snaphop.staticmustache.spec.inheritance;

import com.snaphop.staticmustache.spec.SpecModel;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;

@GenerateRenderableAdapter(template = "inheritance/Datadoesnotoverrideblock.mustache")
@TemplateMapping({
@Template(name="include", template="{{$var}}var in include{{/var}}"),
})
public class Datadoesnotoverrideblock extends SpecModel {
}
