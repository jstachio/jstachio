package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.spec.generator.SpecModel;
import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartials;
import io.jstach.jstache.JStachePartial;

@JStache(path = "inheritance/Inheritindentation.mustache")
@JStachePartials({
		@JStachePartial(name = "parent", template = "stop:\n  {{$nineties}}collaborate and listen{{/nineties}}\n"), })
public class Inheritindentation extends SpecModel {

}
