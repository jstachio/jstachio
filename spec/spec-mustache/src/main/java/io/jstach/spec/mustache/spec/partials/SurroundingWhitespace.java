package io.jstach.spec.mustache.spec.partials;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartials;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "partials/SurroundingWhitespace.mustache")
@JStachePartials({ @JStachePartial(name = "partial", template = "\t|\t"), })
public class SurroundingWhitespace extends SpecModel {

}
