package io.jstach.jstachio.test.graalvm;

import io.jstach.jstache.JStache;

@JStache(template = """

		{{message}}

		""")
public record HelloWorldModel(String message) {

}
