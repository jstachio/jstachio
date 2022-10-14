package io.jstach.examples;

import java.util.Map;

import io.jstach.annotation.GenerateRenderer;
import io.jstach.annotation.TemplateCompilerFlags;
import io.jstach.annotation.TemplateLambda;
import io.jstach.annotation.TemplateCompilerFlags.Flag;

@GenerateRenderer(template = "lambda-example.mustache")
@TemplateCompilerFlags(flags = { Flag.DEBUG })
public record LambdaExample(String name, Map<String, String> props) implements Lambdas {

    @TemplateLambda
    public String hello(String html, String name) {
        return "<hello>" + html + "</hello>: " + name;
    }
}
