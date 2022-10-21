package io.jstach.spec.mustache.spec.partials;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStachePartial;
import io.jstach.annotation.JStachePartialMapping;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "partials/StandaloneWithoutPreviousLine.mustache")
@JStachePartialMapping({ @JStachePartial(name = "partial", template = ">\n>"), })
public class StandaloneWithoutPreviousLine extends SpecModel {

}
