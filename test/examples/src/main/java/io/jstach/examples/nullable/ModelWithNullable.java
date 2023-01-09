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
		{{message.length}}
		""")
public class ModelWithNullable {

	public final List<@Nullable String> names;

	public final @Nullable String @Nullable [] ids;

	private final @Nullable String message;

	public ModelWithNullable(List<@Nullable String> names, @Nullable String @Nullable [] ids, @Nullable String message) {
		super();
		this.names = names;
		this.ids = ids;
		this.message = message;
	}

	public @Nullable String getMessage() {
		return message;
	}

}
