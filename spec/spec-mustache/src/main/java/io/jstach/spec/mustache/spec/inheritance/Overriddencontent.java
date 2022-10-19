package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Overriddencontent.mustache")
@JStachePartialMapping({
@JStachePartial(name="super", template="...{{$title}}Default title{{/title}}..."),
})
public class Overriddencontent extends SpecModel {
}
