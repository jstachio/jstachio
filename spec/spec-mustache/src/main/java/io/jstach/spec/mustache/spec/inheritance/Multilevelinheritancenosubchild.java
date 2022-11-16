package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartial;
import io.jstach.jstache.JStachePartials;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Multilevelinheritancenosubchild.mustache")
@JStachePartials({ @JStachePartial(name = "parent", template = "{{<older}}{{$a}}p{{/a}}{{/older}}"),
		@JStachePartial(name = "older", template = "{{<grandParent}}{{$a}}o{{/a}}{{/grandParent}}"),
		@JStachePartial(name = "grandParent", template = "{{$a}}g{{/a}}"), })
public class Multilevelinheritancenosubchild extends SpecModel {

}
