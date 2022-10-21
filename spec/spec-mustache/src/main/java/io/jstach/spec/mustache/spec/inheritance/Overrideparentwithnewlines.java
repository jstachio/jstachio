package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Overrideparentwithnewlines.mustache")
@JStachePartialMapping({ @JStachePartial(name = "parent", template = "{{$ballmer}}peaking{{/ballmer}}"), })
public class Overrideparentwithnewlines extends SpecModel {

}
