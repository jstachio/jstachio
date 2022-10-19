package io.jstach.spec.mustache.spec.partials;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "partials/BasicBehavior.mustache")
@JStachePartialMapping({
@JStachePartial(name="text", template="from partial"),
})
public class BasicBehavior extends SpecModel {
}
