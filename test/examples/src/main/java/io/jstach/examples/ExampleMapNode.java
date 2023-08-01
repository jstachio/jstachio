package io.jstach.examples;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStache;
import io.jstach.jstachio.context.ObjectContext;

@JStache(path = "example-map-node.mustache")
class ExampleMapNode extends ObjectContext {

	private final Map<String, Object> object = new LinkedHashMap<>();

	@Override
	public @Nullable Object getValue(String key) {
		return object.get(key);
	}

}
