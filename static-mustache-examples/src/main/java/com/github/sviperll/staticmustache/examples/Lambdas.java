package com.github.sviperll.staticmustache.examples;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.sviperll.staticmustache.TemplateLambda;

public interface Lambdas {
    
    
    @TemplateLambda
    default String listProps(String body, Map<String, String> props) {
        return props.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue())
                .collect(Collectors.joining("\n"));
    }
    
    @TemplateLambda
    default KeyValues eachProps(Map<String, String> props) {
        var kvs = props.entrySet().stream().map(e -> new KeyValue(e.getKey(), e.getValue())).toList();
        return new KeyValues(kvs);
    }
    
    public record KeyValues(List<KeyValue> values) {
        
    }
    
    public record KeyValue(String key, String value) {
        
    }

}
