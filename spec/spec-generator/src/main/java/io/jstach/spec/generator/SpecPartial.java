package io.jstach.spec.generator;

public record SpecPartial(String name, String template) {

	String path() {
		return name;
	}

}