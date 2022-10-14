import javax.annotation.processing.Processor;

import io.jstach.examples.JMustacheRenderService;
import io.jstach.spi.RenderService;

module io.jstach.examples {
    requires io.jstach;
    requires java.compiler;
    requires org.kohsuke.metainf_services;
    requires com.samskivert.jmustache;
    
    requires static org.eclipse.jdt.annotation;
    
    opens io.jstach.examples to com.samskivert.jmustache;

    uses Processor;
    uses RenderService;
    provides RenderService with JMustacheRenderService;
}