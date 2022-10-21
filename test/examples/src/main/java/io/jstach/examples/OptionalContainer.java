package io.jstach.examples;

import java.util.Optional;

import io.jstach.annotation.JStache;

@JStache(path = "optional.mustache")
public record OptionalContainer(Optional<String> name, boolean myBoolean, OptionalContainer child) {

}
