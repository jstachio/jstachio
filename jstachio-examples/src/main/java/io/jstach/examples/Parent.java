package io.jstach.examples;

import io.jstach.annotation.JStach;

@JStach(path="parent.mustache")
public record Parent(String message) {
    
}
