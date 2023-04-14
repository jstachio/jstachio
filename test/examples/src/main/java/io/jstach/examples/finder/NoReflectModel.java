package io.jstach.examples.finder;

import io.jstach.jstache.JStache;

@JStache(template = "{{message}}")
record NoReflectModel(String message) {

}
