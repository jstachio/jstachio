package com.github.sviperll.staticmustache.text;

import java.io.IOException;

public interface LayoutFunction {
    
    public RenderFunction withBody(RenderFunction body);
    
    default void render(Appendable a, RenderFunction body) throws IOException {
        withBody(body).render(a);
    }

}
