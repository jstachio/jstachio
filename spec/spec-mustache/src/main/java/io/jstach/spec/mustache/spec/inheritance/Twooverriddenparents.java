package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.spec.generator.SpecModel;
import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartials;
import io.jstach.jstache.JStachePartial;

@JStache(path = "inheritance/Twooverriddenparents.mustache")
@JStachePartials({
		@JStachePartial(name = "parent", template = "|{{$stuff}}...{{/stuff}}{{$default}} default{{/default}}|"), })
public class Twooverriddenparents extends SpecModel {

}
