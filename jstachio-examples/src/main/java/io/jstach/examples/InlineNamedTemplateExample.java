package io.jstach.examples;

import io.jstach.annotation.JStach;
import io.jstach.annotation.JStachPartial;
import io.jstach.annotation.JStachPartialMapping;

@JStach(path = "template-paths-example.mustache")
@JStachPartialMapping(@JStachPartial(name = "formerly-known-as-partial-include", 
    template = """
            I AM INLINED"""))
public record InlineNamedTemplateExample(String name) {

}
