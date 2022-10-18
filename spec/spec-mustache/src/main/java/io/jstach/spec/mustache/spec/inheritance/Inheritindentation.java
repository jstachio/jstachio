package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStach;
import io.jstach.annotation.JStachPartial;
import io.jstach.annotation.JStachPartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStach(path = "inheritance/Inheritindentation.mustache")
@JStachPartialMapping({
@JStachPartial(name="parent", template="stop:\n  {{$nineties}}collaborate and listen{{/nineties}}\n"),
})
public class Inheritindentation extends SpecModel {
}
