package com.snaphop.staticmustache.spec.inheritance;

import com.snaphop.staticmustache.spec.SpecModel;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateCompilerFlags;
import io.jstach.TemplateMapping;
import io.jstach.TemplateCompilerFlags.Flag;

@GenerateRenderableAdapter(template = "inheritance/Multilevelinheritance.mustache")
@TemplateMapping({
@Template(name="parent", template="{{<older}}{{$a}}p{{/a}}{{/older}}"),
@Template(name="older", template="{{<grandParent}}{{$a}}o{{/a}}{{/grandParent}}"),
@Template(name="grandParent", template="{{$a}}g{{/a}}"),
})
@TemplateCompilerFlags(flags= {Flag.DEBUG })
public class Multilevelinheritance extends SpecModel {
}
