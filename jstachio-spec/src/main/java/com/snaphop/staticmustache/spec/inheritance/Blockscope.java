package com.snaphop.staticmustache.spec.inheritance;

import com.snaphop.staticmustache.spec.SpecModel;

import io.jstach.GenerateRenderableAdapter;
import io.jstach.Template;
import io.jstach.TemplateMapping;

@GenerateRenderableAdapter(template = "inheritance/Blockscope.mustache")
@TemplateMapping({
@Template(name="parent", template="{{#nested}}{{$block}}You say {{fruit}}.{{/block}}{{/nested}}"),
})
public class Blockscope extends SpecModel {
}
