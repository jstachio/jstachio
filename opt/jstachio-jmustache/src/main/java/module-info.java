
import io.jstach.jstachio.spi.JStachioServices;
import io.jstach.opt.jmustache.JMustacheRenderer;

/**
 * JMustache extension to JStachio to enable dynamic development of templates.
 * <p>
 * This extension will use JMustache instead of JStachio for rendering. The idea of this
 * extension is to allow you to edit Mustache templates in real time without waiting for
 * the compile reload cycle.
 * <p>
 * If this extension is enabled which it is by default if the ServiceLoader finds it
 * JMustache will be used when a runtime filtered rendering call is made (see
 * {@link io.jstach.jstachio.JStachio}).
 * <p>
 * <strong>See {@link io.jstach.opt.jmustache.JMustacheRenderer}</strong>.
 * <p>
 * 
 * IF you are using modules you might need to:
 * <pre>
 * <code class="language-java">
 * opens your.package.with.templates to io.jstach.jstachio, com.samskivert.jmustache;
 * </code>
 * </pre>
 * 
 * @author agentgt
 *
 * @provides JStachioServices
 */
module io.jstach.opt.jmustache {
	
	exports io.jstach.opt.jmustache;
	
	opens io.jstach.opt.jmustache to io.jstach.jstachio, com.samskivert.jmustache;
	
	requires static java.compiler;
	
	requires transitive io.jstach.jstachio;
	requires transitive com.samskivert.jmustache;

	requires static org.kohsuke.metainf_services;
	requires static org.eclipse.jdt.annotation;

	provides JStachioServices with JMustacheRenderer;
	
	uses javax.annotation.processing.Processor;
}