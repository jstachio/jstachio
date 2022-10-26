package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartials;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Inherit.mustache")
@JStachePartials({ @JStachePartial(name = "include", template = "{{$foo}}default content{{/foo}}"), })
public class Inherit extends SpecModel {

}
