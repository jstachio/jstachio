package io.jstach.spec.mustache.spec.partials;

import io.jstach.annotation.JStach;
import io.jstach.annotation.JStachPartial;
import io.jstach.annotation.JStachPartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStach(path = "partials/PaddingWhitespace.mustache")
@JStachPartialMapping({
@JStachPartial(name="partial", template="[]"),
})
public class PaddingWhitespace extends SpecModel {
}
