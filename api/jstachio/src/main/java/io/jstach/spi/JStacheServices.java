package io.jstach.spi;

import java.io.IOException;

import io.jstach.Appender;
import io.jstach.Formatter;
import io.jstach.RenderFunction;
import io.jstach.Renderer;
import io.jstach.TemplateInfo;

public interface JStacheServices {
    
    default RenderFunction renderer(TemplateInfo template, Object context, RenderFunction previous) throws IOException {
        return previous;
    }
    
    default Formatter formatter(Formatter previous) {
    	return previous;
    }
    
    public static <T> Renderer<T> renderer(Class<T> modelType) {
       return  JStacheServicesResolver._renderer(modelType);
    }
    
    default Formatter formatter() {
        return formatter(Formatter.DefaultFormatter.INSTANCE);
    }
    
    default Appender<? extends Appendable> appender() {
        return Appender.DefaultAppender.INSTANCE;
    }
    
    public static JStacheServices findService() {
        return JStacheServicesResolver.INSTANCE;
    }

}
