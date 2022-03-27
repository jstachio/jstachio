package com.github.sviperll.staticmustache.spi;

import java.io.IOException;

public interface RenderService {
    
    public boolean render(String template, Object context, Appendable a) throws IOException;
    
    public static RenderService findService() {
        return RenderServiceResolver.INSTANCE;
    }

}
