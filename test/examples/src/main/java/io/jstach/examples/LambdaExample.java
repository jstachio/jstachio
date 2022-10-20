package io.jstach.examples;

import java.util.Map;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStacheFlags;
import io.jstach.annotation.JStacheLambda;
import io.jstach.annotation.JStacheFlags.Flag;

@JStache(path = "lambda-example.mustache")
@JStacheFlags(flags = { Flag.DEBUG })
public record LambdaExample(String name, Map<String, String> props) implements Lambdas {

    @JStacheLambda
    public String hello(String html, String name) {
        return "<hello>" + html + "</hello>: " + name;
    }
}
