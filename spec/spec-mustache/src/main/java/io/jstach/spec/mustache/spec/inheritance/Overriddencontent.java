package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.spec.generator.SpecModel;
import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartials;
import io.jstach.annotation.JStachePartial;

@JStache(path = "inheritance/Overriddencontent.mustache")
@JStachePartials({ @JStachePartial(name = "super", template = "...{{$title}}Default title{{/title}}..."), })
public class Overriddencontent extends SpecModel {

}
