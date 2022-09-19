package com.github.sviperll.staticmustache.spi;

import java.io.IOException;

public interface RenderService extends Formatter {
    
    default boolean render(String template, Object context, Appendable a) throws IOException {
    	return false;
    }
    
    default boolean format(Appendable a, String path, Object context) throws IOException {
    	return false;
    }
    
    public static RenderService findService() {
        return RenderServiceResolver.INSTANCE;
    }

}
