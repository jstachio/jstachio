package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStach;
import io.jstach.annotation.JStachPartial;
import io.jstach.annotation.JStachPartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStach(path = "inheritance/Datadoesnotoverrideblockdefault.mustache")
@JStachPartialMapping({
@JStachPartial(name="include", template="{{$var}}var in include{{/var}}"),
})
public class Datadoesnotoverrideblockdefault extends SpecModel {
}
