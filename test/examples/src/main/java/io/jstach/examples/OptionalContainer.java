package io.jstach.examples;

import java.util.Optional;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStacheFlags;
import io.jstach.annotation.JStacheFlags.Flag;

@JStache(path = "optional.mustache")
@JStacheFlags(flags = Flag.DEBUG)
public record OptionalContainer(Optional<String> name, boolean myBoolean, OptionalContainer child) {

}
