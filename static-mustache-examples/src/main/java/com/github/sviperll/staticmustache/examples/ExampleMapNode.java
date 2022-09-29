package com.github.sviperll.staticmustache.examples;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.MapNode;

@GenerateRenderableAdapter(template = "example-map-node.mustache")
public class ExampleMapNode implements MapNode {

    private final Map<String, Object> object = new LinkedHashMap<>();
    
    @Override
    public Object object() {
        return object;
    }
}
