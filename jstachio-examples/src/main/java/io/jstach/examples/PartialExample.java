package io.jstach.examples;

import io.jstach.annotation.GenerateRenderer;

@GenerateRenderer(template="partial-example.mustache")
public record PartialExample(String name) {

}
