package io.jstach.spi;

import java.io.IOException;

import io.jstach.Appender;
import io.jstach.RenderFunction;

public interface RenderService {
    
    default RenderFunction renderer(String template, Object context, RenderFunction previous) throws IOException {
        return previous;
    }
    
    default Formatter formatter(Formatter previous) {
    	return previous;
    }
    
    default Formatter formatter() {
        return formatter(Formatter.DefaultFormatter.INSTANCE);
    }
    
    default Appender appender() {
        return Appender.DefaultAppender.INSTANCE;
    }
    
    public static RenderService findService() {
        return RenderServiceResolver.INSTANCE;
    }

}
