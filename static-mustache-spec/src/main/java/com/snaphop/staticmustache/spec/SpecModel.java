package com.snaphop.staticmustache.spec;

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.sviperll.staticmustache.MapNode;

public class SpecModel implements MapNode {
    
    private final Map<String, Object> object = new LinkedHashMap<>();
    
    @Override
    public Map<String, Object> object() {
        return object;
    }
    
    @Override
    public String toString() {
        return renderString();
    }
    
    public void putAll(Map<? extends String, ? extends Object> m) {
        object().putAll(m);
    }

}
