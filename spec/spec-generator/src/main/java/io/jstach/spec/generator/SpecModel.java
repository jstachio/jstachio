package io.jstach.spec.generator;

import java.util.LinkedHashMap;
import java.util.Map;

import io.jstach.jstachio.context.ContextNode;

public class SpecModel implements ContextNode {

	private final Map<String, Object> object = new LinkedHashMap<>();

	@Override
	public Map<String, Object> object() {
		return object;
	}

	@Override
	public String toString() {
		return renderString();
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		object().putAll(m);
	}

}
