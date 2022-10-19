package io.jstach.spec.mustache.spec.partials;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "partials/SurroundingWhitespace.mustache")
@JStachePartialMapping({
@JStachePartial(name="partial", template="\t|\t"),
})
public class SurroundingWhitespace extends SpecModel {
}
