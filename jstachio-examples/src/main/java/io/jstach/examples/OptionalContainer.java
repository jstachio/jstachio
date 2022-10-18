package io.jstach.examples;

import java.util.Optional;

import io.jstach.annotation.JStach;

@JStach(path="optional.mustache")
public record OptionalContainer(Optional<String> name, boolean myBoolean, OptionalContainer child) {

}
