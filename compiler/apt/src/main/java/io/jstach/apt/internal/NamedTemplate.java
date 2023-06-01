package io.jstach.apt.internal;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public sealed interface NamedTemplate {

	String name();

	Type type();

	Element element();

	AnnotationMirror annotationMirror();

	public enum Type {

		FILE, INLINE

	}

	public record FileTemplate(String name, String path, Element element,
			AnnotationMirror annotationMirror) implements NamedTemplate {
		@Override
		public Type type() {
			return Type.FILE;
		}

		@Override
		public String template() {
			return "";
		}
	}

	public record InlineTemplate(String name, String template, TypeElement element,
			AnnotationMirror annotationMirror) implements NamedTemplate {
		@Override
		public Type type() {
			return Type.INLINE;
		}

		@Override
		public String path() {
			return "";
		}
	}

	public String path();

	public String template();

}
