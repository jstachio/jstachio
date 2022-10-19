package io.jstach;

import java.io.IOException;
import java.io.UncheckedIOException;

public interface Renderer<T> {

    public void render(T model, Appendable appendable) throws IOException;
    
    default void render(T model, StringBuilder sb) {
        try {
            render(model, (Appendable) sb);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    default String render(T model) {
        StringBuilder sb = new StringBuilder();
        render(model, sb);
        return sb.toString();
    }

    public boolean supportsType(Class<?> type);

}
