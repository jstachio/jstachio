package io.jstach.examples;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheFlags.Flag;

@JStache(path = "layouted-nested.mustache")
@JStacheFlags(flags = { Flag.DEBUG })
record LayoutedNestedExample(String title, Data data) {
}

record Data(String message) {
}