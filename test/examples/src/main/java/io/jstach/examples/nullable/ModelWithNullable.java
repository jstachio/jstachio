package io.jstach.examples.nullable;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheFlags.Flag;

@JStache(template = """
		Names:
		{{#names}}
		{{.}}
		{{/names}}

		NullableNames:
		{{#nullableNames}}
		{{.}}
		{{/nullableNames}}

		NamesNullable:
		{{#namesNullable}}
		{{^.}}
		Name missing
		{{/.}}
		{{#.}}
		{{.}}
		{{/.}}
		{{/namesNullable}}

		Ids:
		{{#ids}}
		{{.}}
		{{/ids}}

		NullableMessage:
		{{nullableMessage}}

		NullableMessage Dot:
		{{nullableMessage.length}}

		NullableMessage Condition:
		{{#nullableMessage}}
		{{.}}
		{{/nullableMessage}}

		NullableMessage Invert:
		{{^nullableMessage}}
		nullableMessage was null!
		{{/nullableMessage}}

		Message:
		{{message}}

		Message Dot:
		{{message.length}}

		Message Condition:
		{{#message}}
		{{.}}
		{{/message}}
		""")
@JStacheFlags(flags = { Flag.NO_NULL_CHECKING, Flag.DEBUG })
public class ModelWithNullable {

	public final List<String> names;

	public final List<@Nullable String> namesNullable;

	public final @Nullable List<String> nullableNames;

	public final @Nullable String @Nullable [] ids;

	private final @Nullable String nullableMessage;

	public final String message;

	public ModelWithNullable(List<String> names, //
			List<@Nullable String> namesNullable, //
			@Nullable List<String> nullableNames, //
			@Nullable String @Nullable [] ids, @Nullable String nullableMessage, String message) {
		super();
		this.names = names;
		this.namesNullable = namesNullable;
		this.nullableNames = nullableNames;
		this.ids = ids;
		this.nullableMessage = nullableMessage;
		this.message = message;
	}

	public @Nullable String getNullableMessage() {
		return nullableMessage;
	}

	// @JStacheLambda
	// public Boolean isNull(Object o) {
	// if (o == null) {
	// return true;
	// }
	// return false;
	// }
	//
	// @JStacheLambda
	// public Boolean emptyString(String s) {
	// if (s == null || s.isEmpty()) {
	// return true;
	// }
	// return false;
	// }

}
