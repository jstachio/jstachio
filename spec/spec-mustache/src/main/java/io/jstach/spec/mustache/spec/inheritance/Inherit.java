package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartial;
import io.jstach.jstache.JStachePartials;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Inherit.mustache")
@JStachePartials({ @JStachePartial(name = "include", template = "{{$foo}}default content{{/foo}}"), })
public class Inherit extends SpecModel {

}
