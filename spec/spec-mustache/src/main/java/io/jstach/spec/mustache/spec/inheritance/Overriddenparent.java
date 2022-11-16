package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartial;
import io.jstach.jstache.JStachePartials;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Overriddenparent.mustache")
@JStachePartials({ @JStachePartial(name = "parent", template = "{{$stuff}}...{{/stuff}}"), })
public class Overriddenparent extends SpecModel {

}
