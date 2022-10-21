package io.jstach.examples;

import io.jstach.annotation.JStache;

@JStache(path = "parent.mustache")
public record Parent(String message) {

}
