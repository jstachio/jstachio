package io.jstach.examples;

import io.jstach.annotation.GenerateRenderer;

@GenerateRenderer(template="parent2.mustache")
public record Parent2(String message) {
    
}
