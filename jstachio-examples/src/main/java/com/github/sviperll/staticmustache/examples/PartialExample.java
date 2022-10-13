package com.github.sviperll.staticmustache.examples;

import io.jstach.GenerateRenderableAdapter;

@GenerateRenderableAdapter(template="partial-example.mustache")
public record PartialExample(String name) {

}
