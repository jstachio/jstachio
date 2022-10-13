package com.snaphop.staticmustache.spec.inheritance;

import com.snaphop.staticmustache.spec.SpecModel;
import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.TemplateMapping;
import com.github.sviperll.staticmustache.Template;

@GenerateRenderableAdapter(template = "inheritance/Overriddencontent.mustache")
@TemplateMapping({
@Template(name="super", template="...{{$title}}Default title{{/title}}..."),
})
public class Overriddencontent extends SpecModel {
}
