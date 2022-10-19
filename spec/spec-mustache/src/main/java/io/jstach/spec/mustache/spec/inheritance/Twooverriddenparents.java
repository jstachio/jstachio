package io.jstach.spec.mustache.spec.inheritance;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "inheritance/Twooverriddenparents.mustache")
@JStachePartialMapping({
@JStachePartial(name="parent", template="|{{$stuff}}...{{/stuff}}{{$default}} default{{/default}}|"),
})
public class Twooverriddenparents extends SpecModel {
}
