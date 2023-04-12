package io.jstach.examples.delimiter;

import io.jstach.jstache.JStache;

@JStache(template = """
		{{message}}
		{{=<% %>=}}
		<% message %>
		""")
public record DelimiterExample(String message) {

}
