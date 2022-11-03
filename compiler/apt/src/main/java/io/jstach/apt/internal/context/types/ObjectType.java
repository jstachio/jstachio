package io.jstach.apt.internal.context.types;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public record ObjectType(TypesMixin types, TypeElement typeElement, String typeName) implements KnownType {
	@Override
	public String renderToString(String expression) {
		return "(" + expression + ")";
	}

	@Override
	public String renderClassName() {
		return typeName;
	}

	@Override
	public boolean isSameType(TypeMirror second) {
		return types.isSubtype(typeElement.asType(), second);
	}

	@Override
	public boolean isType(TypeMirror type) {
		return types.isSubtype(type, types.getDeclaredType(typeElement()));
	}

	@Override
	public boolean isSupertype(TypeMirror subtype) {
		return types.isSubtype(subtype, typeElement.asType());
	}

}