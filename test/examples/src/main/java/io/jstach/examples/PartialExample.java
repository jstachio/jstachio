package io.jstach.examples;

import io.jstach.annotation.JStache;

@JStache(path="partial-example.mustache")
public record PartialExample(String name) {

}
