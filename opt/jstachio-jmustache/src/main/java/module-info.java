
import io.jstach.jmustache.JMustacheRenderer;
import io.jstach.spi.JStacheServices;

/**
 * JMustache extension to JStachio to enable seamless fallback and
 * or dynamic development of templates.
 * @author agentgt
 *
 * @provides JStacheServices
 */
module io.jstach.jmustache {
	
	exports io.jstach.jmustache;
	
	opens io.jstach.jmustache to io.jstach, com.samskivert.jmustache;
	
	requires static java.compiler;
	
	requires transitive io.jstach;
	requires transitive com.samskivert.jmustache;

	requires static org.kohsuke.metainf_services;
	requires static org.eclipse.jdt.annotation;

	provides JStacheServices with JMustacheRenderer;
	
	uses javax.annotation.processing.Processor;
}