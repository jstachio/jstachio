package io.jstach.spec.mustache.spec.partials;

import io.jstach.annotation.JStach;
import io.jstach.annotation.JStachPartial;
import io.jstach.annotation.JStachPartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStach(path = "partials/SurroundingWhitespace.mustache")
@JStachPartialMapping({
@JStachPartial(name="partial", template="\t|\t"),
})
public class SurroundingWhitespace extends SpecModel {
}
