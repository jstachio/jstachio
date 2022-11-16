package io.jstach.examples;

import io.jstach.jstache.JStache;

@JStache(template = """
		Hello {{name}}!""")
record InlineExample(String name) {

}
