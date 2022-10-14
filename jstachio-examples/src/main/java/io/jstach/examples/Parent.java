package io.jstach.examples;

import io.jstach.annotation.GenerateRenderableAdapter;

@GenerateRenderableAdapter(template="parent.mustache")
public record Parent(String message) {
    
}
