package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.spec.generator.SpecModel;
import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartials;
import io.jstach.jstache.JStachePartial;

@JStache(path = "inheritance/Blockscope.mustache")
@JStachePartials({
		@JStachePartial(name = "parent", template = "{{#nested}}{{$block}}You say {{fruit}}.{{/block}}{{/nested}}"), })
public class Blockscope extends SpecModel {

}
