package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStach;
import io.jstach.annotation.JStachPartial;
import io.jstach.annotation.JStachPartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStach(path = "inheritance/Overriddencontent.mustache")
@JStachPartialMapping({
@JStachPartial(name="super", template="...{{$title}}Default title{{/title}}..."),
})
public class Overriddencontent extends SpecModel {
}
