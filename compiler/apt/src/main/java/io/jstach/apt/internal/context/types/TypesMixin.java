package io.jstach.apt.internal.context.types;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

public interface TypesMixin {

	public Types getTypes();

	default boolean isSameType(TypeMirror first, TypeMirror second) {
		return getTypes().isSameType(first, second);
	}

	default boolean isSubtype(TypeMirror subtype, TypeMirror supertype) {
		return getTypes().isSubtype(subtype, supertype);
	}

	default DeclaredType getDeclaredType(TypeElement element, TypeMirror... typeArguments) {
		return getTypes().getDeclaredType(element, typeArguments);
	}

	default TypeMirror getArrayType(TypeMirror elementType) {
		return getTypes().getArrayType(elementType);
	}

	default TypeMirror asMemberOf(DeclaredType containing, Element element) {
		return getTypes().asMemberOf(containing, element);
	}

}
