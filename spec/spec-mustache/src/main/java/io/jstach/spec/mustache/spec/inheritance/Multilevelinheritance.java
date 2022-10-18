package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStach;
import io.jstach.annotation.JStachPartial;
import io.jstach.annotation.JStachFlags;
import io.jstach.annotation.JStachPartialMapping;
import io.jstach.annotation.JStachFlags.Flag;
import io.jstach.spec.generator.SpecModel;

@JStach(path = "inheritance/Multilevelinheritance.mustache")
@JStachPartialMapping({
@JStachPartial(name="parent", template="{{<older}}{{$a}}p{{/a}}{{/older}}"),
@JStachPartial(name="older", template="{{<grandParent}}{{$a}}o{{/a}}{{/grandParent}}"),
@JStachPartial(name="grandParent", template="{{$a}}g{{/a}}"),
})
@JStachFlags(flags= {Flag.DEBUG })
public class Multilevelinheritance extends SpecModel {
}
