import io.jstach.jmustache.JMustacheRenderer;
import io.jstach.spi.JStacheServices;

module io.jstach.jmustache {
	
	exports io.jstach.jmustache;
	
	opens io.jstach.jmustache to io.jstach, com.samskivert.jmustache;
	
	requires transitive io.jstach;
	requires transitive com.samskivert.jmustache;

	requires static org.kohsuke.metainf_services;
	requires static org.eclipse.jdt.annotation;

	provides JStacheServices with JMustacheRenderer;
}