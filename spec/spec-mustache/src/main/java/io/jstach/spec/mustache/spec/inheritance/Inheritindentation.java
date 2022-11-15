package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.spec.generator.SpecModel;
import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartials;
import io.jstach.annotation.JStachePartial;

@JStache(path = "inheritance/Inheritindentation.mustache")
@JStachePartials({
		@JStachePartial(name = "parent", template = "stop:\n  {{$nineties}}collaborate and listen{{/nineties}}\n"), })
public class Inheritindentation extends SpecModel {

}
