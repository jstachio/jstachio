
import io.jstach.jstachio.spi.JStacheServices;
import io.jstach.opt.jmustache.JMustacheRenderer;

/**
 * JMustache extension to JStachio to enable seamless fallback and
 * or dynamic development of templates.
 * @author agentgt
 *
 * @provides JStacheServices
 */
module io.jstach.opt.jmustache {
	
	exports io.jstach.opt.jmustache;
	
	opens io.jstach.opt.jmustache to io.jstach.jstachio, com.samskivert.jmustache;
	
	requires static java.compiler;
	
	requires transitive io.jstach.jstachio;
	requires transitive com.samskivert.jmustache;

	requires static org.kohsuke.metainf_services;
	requires static org.eclipse.jdt.annotation;

	provides JStacheServices with JMustacheRenderer;
	
	uses javax.annotation.processing.Processor;
}