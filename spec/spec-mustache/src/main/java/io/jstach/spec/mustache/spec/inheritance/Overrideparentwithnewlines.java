package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStach;
import io.jstach.annotation.JStachPartial;
import io.jstach.annotation.JStachPartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStach(path = "inheritance/Overrideparentwithnewlines.mustache")
@JStachPartialMapping({
@JStachPartial(name="parent", template="{{$ballmer}}peaking{{/ballmer}}"),
})
public class Overrideparentwithnewlines extends SpecModel {
}
