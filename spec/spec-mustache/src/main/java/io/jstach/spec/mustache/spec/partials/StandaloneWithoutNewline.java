package io.jstach.spec.mustache.spec.partials;

import io.jstach.spec.generator.SpecModel;
import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartials;
import io.jstach.annotation.JStachePartial;

@JStache(path = "partials/StandaloneWithoutNewline.mustache")
@JStachePartials({ @JStachePartial(name = "partial", template = ">\n>"), })
public class StandaloneWithoutNewline extends SpecModel {

}
