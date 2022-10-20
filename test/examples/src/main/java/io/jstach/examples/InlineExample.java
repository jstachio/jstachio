package io.jstach.examples;

import io.jstach.annotation.JStache;

@JStache(template = """
        Hello {{name}}!""")
public record InlineExample(String name) {

}
