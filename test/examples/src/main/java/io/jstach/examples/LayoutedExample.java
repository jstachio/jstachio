package io.jstach.examples;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheFlags.Flag;

@JStache(path = "layouted.mustache")
@JStacheFlags(flags = { Flag.DEBUG })
record LayoutedExample(String title, String message) {
}
