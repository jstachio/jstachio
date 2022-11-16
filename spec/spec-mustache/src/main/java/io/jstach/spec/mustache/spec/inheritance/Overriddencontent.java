package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartial;
import io.jstach.jstache.JStachePartials;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Overriddencontent.mustache")
@JStachePartials({ @JStachePartial(name = "super", template = "...{{$title}}Default title{{/title}}..."), })
public class Overriddencontent extends SpecModel {

}
