package io.jstach.spec.mustache.spec.partials;

import io.jstach.spec.generator.SpecModel;
import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartials;
import io.jstach.jstache.JStachePartial;

@JStache(path = "partials/StandaloneIndentation.mustache")
@JStachePartials({ @JStachePartial(name = "partial", template = "|\n{{{content}}}\n|\n"), })
public class StandaloneIndentation extends SpecModel {

}
