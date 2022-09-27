package com.github.sviperll.staticmustache.examples;

import com.github.sviperll.staticmustache.GenerateRenderableAdapter;

@GenerateRenderableAdapter(template="parent.mustache")
public record Parent(String message) {
    
}
