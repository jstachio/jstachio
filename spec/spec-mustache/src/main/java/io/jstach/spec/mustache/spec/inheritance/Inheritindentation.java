package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Inheritindentation.mustache")
@JStachePartialMapping({
		@JStachePartial(name = "parent", template = "stop:\n  {{$nineties}}collaborate and listen{{/nineties}}\n"), })
public class Inheritindentation extends SpecModel {

}
