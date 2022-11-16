package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartial;
import io.jstach.jstache.JStachePartials;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Onlyoneoverride.mustache")
@JStachePartials({ @JStachePartial(name = "parent",
		template = "{{$stuff}}new default one{{/stuff}}, {{$stuff2}}new default two{{/stuff2}}"), })
public class Onlyoneoverride extends SpecModel {

}
