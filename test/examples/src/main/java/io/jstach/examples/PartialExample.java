package io.jstach.examples;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheFlags.Flag;

@JStacheFlags(flags = Flag.DEBUG)
@JStache(path = "partial-example.mustache")
record PartialExample(String name) {

}
