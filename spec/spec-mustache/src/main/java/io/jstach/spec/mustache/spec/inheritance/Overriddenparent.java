package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.spec.generator.SpecModel;
import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartials;
import io.jstach.annotation.JStachePartial;

@JStache(path = "inheritance/Overriddenparent.mustache")
@JStachePartials({ @JStachePartial(name = "parent", template = "{{$stuff}}...{{/stuff}}"), })
public class Overriddenparent extends SpecModel {

}
