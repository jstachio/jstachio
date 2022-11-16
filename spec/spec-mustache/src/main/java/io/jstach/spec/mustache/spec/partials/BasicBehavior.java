package io.jstach.spec.mustache.spec.partials;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartial;
import io.jstach.jstache.JStachePartials;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "partials/BasicBehavior.mustache")
@JStachePartials({ @JStachePartial(name = "text", template = "from partial"), })
public class BasicBehavior extends SpecModel {

}
