package io.jstach.spec.mustache.spec.partials;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartials;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "partials/BasicBehavior.mustache")
@JStachePartials({ @JStachePartial(name = "text", template = "from partial"), })
public class BasicBehavior extends SpecModel {

}
