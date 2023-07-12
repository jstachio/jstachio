package io.jstach.opt.dropwizard.example;

import io.jstach.jstache.JStache;
import io.jstach.opt.dropwizard.JStachioViewSupport;

@JStache
public record ExampleModel(String message) implements JStachioViewSupport {

}
