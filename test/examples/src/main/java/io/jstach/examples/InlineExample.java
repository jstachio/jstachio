package io.jstach.examples;

import io.jstach.annotation.JStache;

@JStache(template = """
		Hello {{name}}!""")
record InlineExample(String name) {

}
