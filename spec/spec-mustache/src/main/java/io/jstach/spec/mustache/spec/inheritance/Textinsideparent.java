package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Textinsideparent.mustache")
@JStachePartialMapping({ @JStachePartial(name = "parent", template = "{{$foo}}default content{{/foo}}"), })
public class Textinsideparent extends SpecModel {

}
