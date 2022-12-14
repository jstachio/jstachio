package io.jstach.examples.jstacheifaces;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstachio.Template;

public abstract class AbstractSome<T> implements Template<T> {

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

	public void execute(T model, Appendable a) throws IOException {
		execute(model, a, templateFormatter(), templateEscaper());
	}

}
