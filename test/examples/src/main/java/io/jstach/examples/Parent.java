package io.jstach.examples;

import io.jstach.annotation.JStache;

@JStache(path = "parent.mustache")
record Parent(String message) {

}
