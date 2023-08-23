
import io.jstach.jstachio.spi.JStachioExtension;
import io.jstach.opt.jmustache.JMustacheRenderer;

/**
 * JMustache extension to enable dynamic development of templates:
 * <a href="https://repo1.maven.org/maven2/io/jstach/jstachio-jmustache/maven-metadata.xml" class="gav">io.jstach:jstachio-jmustache</a>.
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
 * @provides JStachioExtension
 * @deprecated This extension does not reliably mimic JStachio's mustache support
 * unfortunately based on feedback we have decided to deprecate this and recommend
 * using other mechanisms for hot reload. 
 */
@Deprecated
module io.jstach.opt.jmustache {
	
	exports io.jstach.opt.jmustache;
	
	opens io.jstach.opt.jmustache to io.jstach.jstachio, com.samskivert.jmustache;
	
	requires static java.compiler;
	
	requires transitive io.jstach.jstachio;
	requires transitive com.samskivert.jmustache;

	requires static io.jstach.svc;
	requires static org.eclipse.jdt.annotation;

	provides JStachioExtension with JMustacheRenderer;
	
	uses javax.annotation.processing.Processor;
}