package io.jstach.examples;

import java.util.Optional;

import io.jstach.jstache.JStache;

@JStache(path = "optional.mustache")
// @JStacheFlags(flags = Flag.DEBUG)
record OptionalContainer(Optional<String> name, boolean myBoolean, Optional<OptionalContainer> child) {

}
