package com.github.sviperll.staticmustache.examples;

import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.TemplateLambda;

@GenerateRenderableAdapter(template = "lambda-example.mustache")
public record LambdaExample(String name) {

    @TemplateLambda
    public String hello(String html) {
        return "<hello>" + html + "</hello>";
    }
}
