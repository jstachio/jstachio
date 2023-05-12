package io.jstach.examples.stackoverflow;

import io.jstach.jstache.JStache;

@JStache(template = """
		{{#word1}}
		{{.}}
		{{/word1}}
		{{#word2}}
		{{.}}
		{{/word2}}
		{{#word3}}
		{{.}}
		{{/word3}}
		{{#word4}}
		{{.}}
		{{/word4}}
		{{#word5}}
		{{.}}
		{{/word5}}
		""")
public record HowToStripBlankLines(String word1, String word2, String word3, String word4, String word5) {

}
