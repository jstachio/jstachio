package io.jstach.spec.mustache.spec.partials;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartial;
import io.jstach.jstache.JStachePartials;
import io.jstach.spec.generator.SpecModel;

@JStache(path = "partials/InlineIndentation.mustache")
@JStachePartials({ @JStachePartial(name = "partial", template = ">\n>"), })
public class InlineIndentation extends SpecModel {

}
