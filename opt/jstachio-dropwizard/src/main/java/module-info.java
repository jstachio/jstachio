import io.jstach.opt.dropwizard.JStachioViewRenderer;

/**
 * JStachio Dropwizard integration:
 * <a href="https://repo1.maven.org/maven2/io/jstach/jstachio-dropwizard/maven-metadata.xml" class="gav">io.jstach:jstachio-dropwizard</a>.
 * <pre><code class="language-java">
 * bootstrap.addBundle(new ViewBundle&lt;&gt;());
 * </code>
 * </pre>
 * @author agentgt
 * @see io.jstach.opt.dropwizard.DropwizardJStacheConfig
 * @provides io.dropwizard.views.common.ViewRenderer JStachio view renderer
 */
module io.jstach.opt.dropwizard {
	requires transitive io.jstach.jstachio;
	requires io.dropwizard.views;
	requires static org.eclipse.jdt.annotation;
	exports io.jstach.opt.dropwizard;
	
	provides io.dropwizard.views.common.ViewRenderer with JStachioViewRenderer;
}