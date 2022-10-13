import javax.annotation.processing.Processor;
import com.github.sviperll.staticmustache.examples.JMustacheRenderService;

import io.jstach.spi.RenderService;

module com.snaphop.staticmustache.example {
    requires io.jstach;
    requires java.compiler;
    requires org.kohsuke.metainf_services;
    requires com.samskivert.jmustache;
    
    requires static org.eclipse.jdt.annotation;
    
    opens com.github.sviperll.staticmustache.examples to com.samskivert.jmustache;

    uses Processor;
    uses RenderService;
    provides RenderService with JMustacheRenderService;
}