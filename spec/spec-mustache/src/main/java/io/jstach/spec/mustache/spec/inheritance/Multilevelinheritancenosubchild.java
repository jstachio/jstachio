package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Multilevelinheritancenosubchild.mustache")
@JStachePartialMapping({ @JStachePartial(name = "parent", template = "{{<older}}{{$a}}p{{/a}}{{/older}}"),
		@JStachePartial(name = "older", template = "{{<grandParent}}{{$a}}o{{/a}}{{/grandParent}}"),
		@JStachePartial(name = "grandParent", template = "{{$a}}g{{/a}}"), })
public class Multilevelinheritancenosubchild extends SpecModel {

}
