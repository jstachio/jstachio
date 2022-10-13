package com.snaphop.staticmustache.spec.inheritance;

import com.snaphop.staticmustache.spec.SpecModel;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;

@GenerateRenderableAdapter(template = "inheritance/Inheritindentation.mustache")
@TemplateMapping({
@Template(name="parent", template="stop:\n  {{$nineties}}collaborate and listen{{/nineties}}\n"),
})
public class Inheritindentation extends SpecModel {
}
