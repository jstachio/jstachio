package com.github.sviperll.staticmustache.context.types;

import javax.lang.model.element.TypeElement;

public record ObjectType(TypeElement typeElement) implements KnownType {
    @Override
    public String renderToString(String expression) {
        return "(" + expression + ")".toString();
    }
}