package io.jstach.spec.mustache.spec.partials;

import io.jstach.spec.generator.SpecModel;
import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartials;
import io.jstach.jstache.JStachePartial;

@JStache(path = "partials/PaddingWhitespace.mustache")
@JStachePartials({ @JStachePartial(name = "partial", template = "[]"), })
public class PaddingWhitespace extends SpecModel {

}
