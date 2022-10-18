package io.jstach.examples;

import java.util.Map;

import io.jstach.annotation.JStach;
import io.jstach.annotation.JStachFlags;
import io.jstach.annotation.JStachLambda;
import io.jstach.annotation.JStachFlags.Flag;

@JStach(path = "lambda-example.mustache")
@JStachFlags(flags = { Flag.DEBUG })
public record LambdaExample(String name, Map<String, String> props) implements Lambdas {

    @JStachLambda
    public String hello(String html, String name) {
        return "<hello>" + html + "</hello>: " + name;
    }
}
