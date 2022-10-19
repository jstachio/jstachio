package io.jstach.spi;

import java.io.IOException;
import java.util.List;

import io.jstach.Appender;
import io.jstach.Formatter;
import io.jstach.RenderFunction;
import io.jstach.Renderer;
import io.jstach.TemplateInfo;

public interface JStachServices {
    
    default RenderFunction renderer(TemplateInfo template, Object context, RenderFunction previous) throws IOException {
        return previous;
    }
    
    default Formatter formatter(Formatter previous) {
    	return previous;
    }
    
    default List<Renderer<?>> findRenderers() {
        return List.of();
    }
    
    default Formatter formatter() {
        return formatter(Formatter.DefaultFormatter.INSTANCE);
    }
    
    default Appender<? extends Appendable> appender() {
        return Appender.DefaultAppender.INSTANCE;
    }
    
    public static JStachServices findService() {
        return JStachServicesResolver.INSTANCE;
    }

}
