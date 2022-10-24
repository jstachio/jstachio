package io.jstach.apt.context.types;

import javax.lang.model.type.TypeMirror;

public record NativeType(TypesMixin types, TypeMirror typeMirror, Class<?> boxedType,
		Class<?> unboxedType) implements KnownType {

	@Override
	public String renderToString(String expression) {
		return boxedType.getName() + ".toString(" + expression + ")";
	}

	public String renderClassName() {
		return unboxedType.getName();
	}

	@Override
	public boolean isSameType(TypeMirror second) {
		return types.isSameType(typeMirror, second);
	}

	@Override
	public boolean isSupertype(TypeMirror subtype) {
		return false;
	}

	@Override
	public boolean isType(TypeMirror type) {
		return isSameType(type);
	}

}