package com.snaphop.staticmustache.spec.inheritance;

import com.snaphop.staticmustache.spec.SpecModel;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;

@GenerateRenderableAdapter(template = "inheritance/Overrideparentwithnewlines.mustache")
@TemplateMapping({
@Template(name="parent", template="{{$ballmer}}peaking{{/ballmer}}"),
})
public class Overrideparentwithnewlines extends SpecModel {
}
