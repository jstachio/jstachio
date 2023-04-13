package io.jstach.spec.mustache.spec.delimiters;

import io.jstach.spec.generator.SpecModel;
import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePartials;
import io.jstach.jstache.JStachePartial;

@JStache(path = "delimiters/PartialInheritence.mustache")
@JStachePartials({ @JStachePartial(name = "include", template = ".{{value}}."), })
public class PartialInheritence extends SpecModel {

}
