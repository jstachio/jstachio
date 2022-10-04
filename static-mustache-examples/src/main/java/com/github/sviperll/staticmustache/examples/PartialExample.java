package com.github.sviperll.staticmustache.examples;

import com.github.sviperll.staticmustache.GenerateRenderableAdapter;

@GenerateRenderableAdapter(template="partial-example.mustache")
public record PartialExample(String name) {

}
