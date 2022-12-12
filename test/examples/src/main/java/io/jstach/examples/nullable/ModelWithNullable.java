package io.jstach.examples.nullable;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStache;

@JStache(template = """
		{{#names}}
		{{.}}
		{{/names}}
		{{#ids}}
		{{.}}
		{{/ids}}
		""")
public class ModelWithNullable {

	public final List<@Nullable String> names;

	public final @Nullable String @Nullable [] ids;

	public ModelWithNullable(List<@Nullable String> names, @Nullable String @Nullable [] ids) {
		super();
		this.names = names;
		this.ids = ids;
	}

}
