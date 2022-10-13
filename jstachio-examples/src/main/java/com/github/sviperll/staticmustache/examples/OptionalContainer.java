package com.github.sviperll.staticmustache.examples;

import java.util.Optional;

import com.github.sviperll.staticmustache.GenerateRenderableAdapter;

@GenerateRenderableAdapter(template="optional.mustache")
public record OptionalContainer(Optional<String> name, boolean myBoolean, OptionalContainer child) {

}
