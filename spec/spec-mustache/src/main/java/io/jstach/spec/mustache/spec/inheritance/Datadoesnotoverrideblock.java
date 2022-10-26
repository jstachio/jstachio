package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartials;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Datadoesnotoverrideblock.mustache")
@JStachePartials({ @JStachePartial(name = "include", template = "{{$var}}var in include{{/var}}"), })
public class Datadoesnotoverrideblock extends SpecModel {

}
