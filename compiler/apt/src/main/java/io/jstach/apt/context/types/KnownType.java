package io.jstach.apt.context.types;

import javax.lang.model.type.TypeMirror;

public interface KnownType {

	public String renderToString(String expression);

	public String renderClassName();

	boolean isSupertype(TypeMirror subtype);

	boolean isSameType(TypeMirror second);

	public boolean isType(TypeMirror type);

}