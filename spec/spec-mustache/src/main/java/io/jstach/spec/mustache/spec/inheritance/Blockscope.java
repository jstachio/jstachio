package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Blockscope.mustache")
@JStachePartialMapping({
		@JStachePartial(name = "parent", template = "{{#nested}}{{$block}}You say {{fruit}}.{{/block}}{{/nested}}"), })
public class Blockscope extends SpecModel {

}
