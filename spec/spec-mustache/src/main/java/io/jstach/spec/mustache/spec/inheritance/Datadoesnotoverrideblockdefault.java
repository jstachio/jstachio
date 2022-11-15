package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.spec.generator.SpecModel;
import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartials;
import io.jstach.annotation.JStachePartial;

@JStache(path = "inheritance/Datadoesnotoverrideblockdefault.mustache")
@JStachePartials({ @JStachePartial(name = "include", template = "{{$var}}var in include{{/var}}"), })
public class Datadoesnotoverrideblockdefault extends SpecModel {

}
