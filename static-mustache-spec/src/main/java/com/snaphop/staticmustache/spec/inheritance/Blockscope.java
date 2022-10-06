package com.snaphop.staticmustache.spec.inheritance;

import com.snaphop.staticmustache.spec.SpecModel;
import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.TemplateMapping;
import com.github.sviperll.staticmustache.Template;

@GenerateRenderableAdapter(template = "inheritance/Blockscope.mustache")
@TemplateMapping({
@Template(name="parent", template="{{#nested}}{{$block}}You say {{fruit}}.{{/block}}{{/nested}}"),
})
public class Blockscope extends SpecModel {
}
