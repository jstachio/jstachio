package com.github.sviperll.staticmustache.spi;

import java.io.IOException;

import com.github.sviperll.staticmustache.text.RenderFunction;

public interface RenderService {
    
    default RenderFunction renderer(String template, Object context, RenderFunction previous) throws IOException {
        return previous;
    }
    
    default Formatter formatter(String path, Object context, Formatter previous) throws IOException {
    	return previous;
    }
    
    default Formatter formatter(String path, Object context) throws IOException {
        return formatter(path, context, Formatter.DefaultFormatter.INSTANCE);
    }
    
    public static RenderService findService() {
        return RenderServiceResolver.INSTANCE;
    }

}
