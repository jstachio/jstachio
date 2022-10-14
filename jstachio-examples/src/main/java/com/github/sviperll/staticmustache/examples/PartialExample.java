package com.github.sviperll.staticmustache.examples;

import io.jstach.annotation.GenerateRenderableAdapter;

@GenerateRenderableAdapter(template="partial-example.mustache")
public record PartialExample(String name) {

}
