package io.jstach.examples.jstacheifaces;

import org.eclipse.jdt.annotation.Nullable;

public class AbstractSome {

	private final String name;

	public AbstractSome() {
		this.name = "none";
	}

	@SomeAnnotation(value = "hello")
	public AbstractSome(@SomeAnnotation @Nullable String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
