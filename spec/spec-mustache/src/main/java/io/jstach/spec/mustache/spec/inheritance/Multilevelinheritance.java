package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStacheFlags;
import io.jstach.annotation.JStachePartials;
import io.jstach.annotation.JStacheFlags.Flag;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Multilevelinheritance.mustache")
@JStachePartials({ @JStachePartial(name = "parent", template = "{{<older}}{{$a}}p{{/a}}{{/older}}"),
		@JStachePartial(name = "older", template = "{{<grandParent}}{{$a}}o{{/a}}{{/grandParent}}"),
		@JStachePartial(name = "grandParent", template = "{{$a}}g{{/a}}"), })
@JStacheFlags(flags = { Flag.DEBUG })
public class Multilevelinheritance extends SpecModel {

}
