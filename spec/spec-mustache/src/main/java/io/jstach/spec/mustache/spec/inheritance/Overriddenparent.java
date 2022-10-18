package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStach;
import io.jstach.annotation.JStachPartial;
import io.jstach.annotation.JStachPartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStach(path = "inheritance/Overriddenparent.mustache")
@JStachPartialMapping({
@JStachPartial(name="parent", template="{{$stuff}}...{{/stuff}}"),
})
public class Overriddenparent extends SpecModel {
}
