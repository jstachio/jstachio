import javax.annotation.processing.Processor;

import io.jstach.Renderer;
import io.jstach.examples.JMustacheRenderService;
import io.jstach.spi.JStachServices;

module io.jstach.examples {
    requires transitive io.jstach;
    requires java.compiler;
    requires org.kohsuke.metainf_services;
    requires com.samskivert.jmustache;
    requires org.mapstruct;
    
    requires static org.eclipse.jdt.annotation;
    
    opens io.jstach.examples to com.samskivert.jmustache, io.jstach;
    
    exports io.jstach.examples to org.mapstruct;

    
    uses Processor;
    uses JStachServices;
    uses Renderer;
    
    provides JStachServices with JMustacheRenderService;
}