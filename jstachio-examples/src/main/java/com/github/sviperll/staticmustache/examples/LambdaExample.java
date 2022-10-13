package com.github.sviperll.staticmustache.examples;

import java.util.Map;

import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.TemplateCompilerFlags;
import com.github.sviperll.staticmustache.TemplateCompilerFlags.Flag;
import com.github.sviperll.staticmustache.TemplateLambda;

@GenerateRenderableAdapter(template = "lambda-example.mustache")
@TemplateCompilerFlags(flags = { Flag.DEBUG })
public record LambdaExample(String name, Map<String, String> props) implements Lambdas {

    @TemplateLambda
    public String hello(String html, String name) {
        return "<hello>" + html + "</hello>: " + name;
    }
}
