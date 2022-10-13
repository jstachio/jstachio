package com.github.sviperll.staticmustache.examples;

import java.util.LinkedHashMap;
import java.util.Map;

import io.jstach.ContextNode;
import io.jstach.GenerateRenderableAdapter;

@GenerateRenderableAdapter(template = "example-map-node.mustache")
public class ExampleMapNode implements ContextNode {

    private final Map<String, Object> object = new LinkedHashMap<>();
    
    @Override
    public Object object() {
        return object;
    }
}
