package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStach;
import io.jstach.annotation.JStachPartial;
import io.jstach.annotation.JStachPartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStach(path = "inheritance/Parenttemplate.mustache")
@JStachPartialMapping({
@JStachPartial(name="parent", template="{{$foo}}default content{{/foo}}"),
})
public class Parenttemplate extends SpecModel {
}
