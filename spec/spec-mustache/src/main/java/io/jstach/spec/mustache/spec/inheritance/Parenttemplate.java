package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.spec.generator.SpecModel;
import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartials;
import io.jstach.annotation.JStachePartial;

@JStache(path = "inheritance/Parenttemplate.mustache")
@JStachePartials({ @JStachePartial(name = "parent", template = "{{$foo}}default content{{/foo}}"), })
public class Parenttemplate extends SpecModel {

}
