package io.jstach.examples;

import java.util.Optional;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheFlags.Flag;

@JStache(path = "optional.mustache")
@JStacheFlags(flags = Flag.DEBUG)
record OptionalContainer(Optional<String> name, boolean myBoolean, Optional<OptionalContainer> child) {

}
