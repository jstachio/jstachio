package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartials;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Onlyoneoverride.mustache")
@JStachePartials({ @JStachePartial(name = "parent",
		template = "{{$stuff}}new default one{{/stuff}}, {{$stuff2}}new default two{{/stuff2}}"), })
public class Onlyoneoverride extends SpecModel {

}
