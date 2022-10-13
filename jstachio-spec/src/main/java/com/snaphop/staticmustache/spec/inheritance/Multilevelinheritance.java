package com.snaphop.staticmustache.spec.inheritance;

import com.snaphop.staticmustache.spec.SpecModel;
import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.TemplateMapping;
import com.github.sviperll.staticmustache.Template;
import com.github.sviperll.staticmustache.TemplateCompilerFlags;
import com.github.sviperll.staticmustache.TemplateCompilerFlags.Flag;

@GenerateRenderableAdapter(template = "inheritance/Multilevelinheritance.mustache")
@TemplateMapping({
@Template(name="parent", template="{{<older}}{{$a}}p{{/a}}{{/older}}"),
@Template(name="older", template="{{<grandParent}}{{$a}}o{{/a}}{{/grandParent}}"),
@Template(name="grandParent", template="{{$a}}g{{/a}}"),
})
@TemplateCompilerFlags(flags= {Flag.DEBUG })
public class Multilevelinheritance extends SpecModel {
}
