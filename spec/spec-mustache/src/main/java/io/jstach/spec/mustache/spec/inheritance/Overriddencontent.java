package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStachePartial;
import io.jstach.jstache.JStachePartials;
import io.jstach.jstache.JStacheFlags.Flag;
import io.jstach.spec.generator.SpecModel;

@JStacheFlags(flags = Flag.DEBUG)
@JStache(path = "inheritance/Overriddencontent.mustache")
@JStachePartials({ @JStachePartial(name = "super", template = "...{{$title}}Default title{{/title}}..."), })
public class Overriddencontent extends SpecModel {

}
