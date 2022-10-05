package com.snaphop.staticmustache.spec.partials;

import com.snaphop.staticmustache.spec.SpecModel;
import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.TemplateMapping;
import com.github.sviperll.staticmustache.Template;
import com.github.sviperll.staticmustache.TemplateCompilerFlags;
import com.github.sviperll.staticmustache.TemplateCompilerFlags.Flag;

@TemplateCompilerFlags(flags = {Flag.DEBUG})
@GenerateRenderableAdapter(template = "partials/InlineIndentation.mustache")
@TemplateMapping({
@Template(name="partial", template=">\n>"),
})
public class InlineIndentation extends SpecModel {
}
