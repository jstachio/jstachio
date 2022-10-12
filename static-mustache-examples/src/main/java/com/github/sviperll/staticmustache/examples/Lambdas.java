package com.github.sviperll.staticmustache.examples;

import java.util.Map;
import java.util.stream.Collectors;

import com.github.sviperll.staticmustache.TemplateLambda;

public interface Lambdas {
    
//    @TemplateLambda
//    default String simpleExample(String s) {
//        return "<b>" + s + "</b>";
//    }
//    
//    public record Model(String name) {}
//    
//    @TemplateLambda(template="""
//            <b>Hello {{name}}</b>
//            """)
//    default Model returnTemplate(String name) {
//        return new Model(name);
//    }
    
    @TemplateLambda
    default String listProps(String body, Map<String, String> props) {
        return props.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue())
                .collect(Collectors.joining("\n"));
    }

}
