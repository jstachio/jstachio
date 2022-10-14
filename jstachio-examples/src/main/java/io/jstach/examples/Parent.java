package io.jstach.examples;

import io.jstach.annotation.GenerateRenderer;

@GenerateRenderer(template="parent.mustache")
public record Parent(String message) {
    
}
