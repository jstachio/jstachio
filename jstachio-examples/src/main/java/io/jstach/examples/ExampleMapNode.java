package io.jstach.examples;

import java.util.LinkedHashMap;
import java.util.Map;

import io.jstach.annotation.JStach;
import io.jstach.context.ContextNode;

@JStach(path = "example-map-node.mustache")
public class ExampleMapNode implements ContextNode {

    private final Map<String, Object> object = new LinkedHashMap<>();
    
    @Override
    public Object object() {
        return object;
    }
}
