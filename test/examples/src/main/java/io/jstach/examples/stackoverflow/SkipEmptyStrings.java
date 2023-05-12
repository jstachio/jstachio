package io.jstach.examples.stackoverflow;

import io.jstach.jstache.JStache;

// https://stackoverflow.com/questions/74746407/how-to-skip-empty-string-with-mustache
@JStache(template = """
		{{alpha}}
		{{^romeo.empty}}
		{{#romeo}}
		{{romeo}}


		{{/romeo}}
		{{/romeo.empty}}
		{{julia}}
		""")
public record SkipEmptyStrings(String alpha, String romeo, String julia) {

}
