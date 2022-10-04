package com.snaphop.staticmustache.spec.partials;

import com.snaphop.staticmustache.spec.SpecModel;
import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.TemplateMapping;
import com.github.sviperll.staticmustache.Template;
import com.github.sviperll.staticmustache.TemplateCompilerFlags;
import com.github.sviperll.staticmustache.TemplateCompilerFlags.Flag;

@TemplateCompilerFlags(flags = {Flag.DEBUG})
@GenerateRenderableAdapter(template = "partials/SurroundingWhitespace.mustache")
@TemplateMapping({
@Template(name="partial", template="""
	|	"""
),
})
public class SurroundingWhitespace extends SpecModel {
}
