package com.snaphop.staticmustache.spec.inheritance;

import com.snaphop.staticmustache.spec.SpecModel;
import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.TemplateMapping;
import com.github.sviperll.staticmustache.Template;

@GenerateRenderableAdapter(template = "inheritance/Overrideparentwithnewlines.mustache")
@TemplateMapping({
@Template(name="parent", template="{{$ballmer}}peaking{{/ballmer}}"),
})
public class Overrideparentwithnewlines extends SpecModel {
}
