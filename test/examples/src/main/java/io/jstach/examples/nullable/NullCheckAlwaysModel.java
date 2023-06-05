package io.jstach.examples.nullable;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheFlags;

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
@JStacheFlags(flags = {}, nullableAnnotation = Nullable.class)
public record NullCheckAlwaysModel(List<@Nullable String> names) {

}
