package com.github.sviperll.staticmustache.examples;

import com.github.sviperll.staticmustache.TemplateLambda;

public interface Lambdas {
    
    @TemplateLambda
    default String simpleExample(String s) {
        return "<b>" + s + "</b>";
    }
    
    public record Model(String name) {}
    
    @TemplateLambda(template="""
            <b>Hello {{name}}</b>
            """)
    default Model returnTemplate(String name) {
        return new Model(name);
    }

}
