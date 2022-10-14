package io.jstach.examples;

import java.util.Optional;

import io.jstach.annotation.GenerateRenderer;

@GenerateRenderer(template="optional.mustache")
public record OptionalContainer(Optional<String> name, boolean myBoolean, OptionalContainer child) {

}
