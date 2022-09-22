import javax.annotation.processing.Processor;
import com.github.sviperll.staticmustache.examples.JMustacheRenderService;
import com.github.sviperll.staticmustache.spi.RenderService;

module com.snaphop.staticmustache.example {
    requires com.snaphop.staticmustache;
    requires java.compiler;
    requires org.kohsuke.metainf_services;
    requires com.samskivert.jmustache;
    
    opens com.github.sviperll.staticmustache.examples;
    
    uses Processor;
    uses RenderService;
    provides RenderService with JMustacheRenderService;
}