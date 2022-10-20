package io.jstach.apt.context.types;

public interface KnownType {
    public String renderToString(String expression);
    public String renderClassName();
}