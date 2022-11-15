package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.spec.generator.SpecModel;
import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartials;
import io.jstach.annotation.JStachePartial;

@JStache(path = "inheritance/Multilevelinheritancenosubchild.mustache")
@JStachePartials({ @JStachePartial(name = "parent", template = "{{<older}}{{$a}}p{{/a}}{{/older}}"),
		@JStachePartial(name = "older", template = "{{<grandParent}}{{$a}}o{{/a}}{{/grandParent}}"),
		@JStachePartial(name = "grandParent", template = "{{$a}}g{{/a}}"), })
public class Multilevelinheritancenosubchild extends SpecModel {

}
