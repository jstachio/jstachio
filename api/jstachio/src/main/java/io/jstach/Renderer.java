package io.jstach;

import java.io.IOException;

public interface Renderer<T> {

    public void render(T model, Appendable appendable) throws IOException;

    // public RenderFunction render(T model);

    public boolean supportsType(Class<?> type);

}
