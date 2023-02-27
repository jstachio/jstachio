package io.jstach.examples.using;

import io.jstach.jstache.JStache;

@JStache(template = "{{message}}")
public record MyConfigModel(String message) {

}
