package com.snaphop.staticmustache.spec.inheritance;

import com.snaphop.staticmustache.spec.SpecModel;
import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.TemplateMapping;
import com.github.sviperll.staticmustache.Template;

@GenerateRenderableAdapter(template = "inheritance/Inheritindentation.mustache")
@TemplateMapping({
@Template(name="parent", template="stop:\n  {{$nineties}}collaborate and listen{{/nineties}}\n"),
})
public class Inheritindentation extends SpecModel {
}
