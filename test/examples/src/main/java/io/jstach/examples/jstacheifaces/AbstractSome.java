package io.jstach.examples.jstacheifaces;

public class AbstractSome {

	private final String name;

	public AbstractSome() {
		this.name = "none";
	}

	@SomeAnnotation
	public AbstractSome(@SomeAnnotation /* @Nullable */ String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
