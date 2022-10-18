import javax.annotation.processing.Processor;

import io.jstach.examples.JMustacheRenderService;
import io.jstach.spi.TemplateServices;

module io.jstach.examples {
    requires io.jstach;
    requires java.compiler;
    requires org.kohsuke.metainf_services;
    requires com.samskivert.jmustache;
    
    requires static org.eclipse.jdt.annotation;
    
    opens io.jstach.examples to com.samskivert.jmustache;

    uses Processor;
    uses TemplateServices;
    provides TemplateServices with JMustacheRenderService;
}