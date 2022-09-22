package com.github.sviperll.staticmustache.context.types;

import javax.lang.model.type.TypeMirror;

public record NativeType(TypeMirror typeMirror, Class<?> boxedType, Class<?> unboxedType) implements KnownType {
    
    @Override
    public String renderToString(String expression) {
        return boxedType.getName() + ".toString(" + expression + ")";
    }
    
    public String renderClassName() {
        return unboxedType.getName();
    }

}