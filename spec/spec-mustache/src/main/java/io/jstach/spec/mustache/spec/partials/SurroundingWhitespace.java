package io.jstach.spec.mustache.spec.partials;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartial;
import io.jstach.jstache.JStachePartials;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "partials/SurroundingWhitespace.mustache")
@JStachePartials({ @JStachePartial(name = "partial", template = "\t|\t"), })
public class SurroundingWhitespace extends SpecModel {

}
