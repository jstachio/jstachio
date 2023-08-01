package io.jstach.spec.generator;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstachio.context.ObjectContext;

public class SpecModel extends ObjectContext {

	private final Map<String, Object> object = new LinkedHashMap<>();

	@Override
	public @Nullable Object getValue(String key) {
		return object.get(key);
	}

	@Override
	public String toString() {
		return renderString();
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		object.putAll(m);
	}

}
