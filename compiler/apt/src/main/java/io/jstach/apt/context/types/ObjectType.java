package io.jstach.apt.context.types;

import javax.lang.model.element.TypeElement;

public record ObjectType(TypeElement typeElement, Class<?> type) implements KnownType {
    @Override
    public String renderToString(String expression) {
        return "(" + expression + ")";
    }
    
    @Override
    public String renderClassName() {
        return type.getName();
    }
}