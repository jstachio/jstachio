package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Onlyoneoverride.mustache")
@JStachePartialMapping({
@JStachePartial(name="parent", template="{{$stuff}}new default one{{/stuff}}, {{$stuff2}}new default two{{/stuff2}}"),
})
public class Onlyoneoverride extends SpecModel {
}
