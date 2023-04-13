package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.spec.generator.SpecModel;
import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartials;
import io.jstach.jstache.JStachePartial;

@JStache(path = "inheritance/Overriddencontent.mustache")
@JStachePartials({ @JStachePartial(name = "super", template = "...{{$title}}Default title{{/title}}..."), })
public class Overriddencontent extends SpecModel {

}
