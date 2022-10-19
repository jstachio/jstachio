package io.jstach.examples;

import io.jstach.annotation.JStach;

@JStach(template = """
        Hello {{name}}!""")
public record InlineExample(String name) {

}
