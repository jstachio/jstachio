package io.jstach.examples.nullable;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStache;

@JStache(template = """
		{{#names}}
		{{#-first}}
		---------
		{{/-first}}
		{{#.}}
		{{.}}
		{{/.}}
		{{^.}}
		Missing for index: {{@index}}
		{{/.}}
		Index: {{@index}}
		---------
		{{/names}}
		""")
public record NullCheckAlwaysModel(List<@Nullable String> names) {

}
