import io.jstach.opt.dropwizard.JStachioViewRenderer;

/**
 * JStachio Dropwizard integration:
 * <a href="https://repo1.maven.org/maven2/io/jstach/jstachio-dropwizard/maven-metadata.xml" class="gav">io.jstach:jstachio-dropwizard</a>.
 * <p>
 * <strong>Step 1: add ViewBundle</strong>
 * <pre><code class="language-java">
 * bootstrap.addBundle(new ViewBundle&lt;&gt;());
 * </code>
 * </pre>
 * <strong>Step 2: annotate package-info or module-info</strong>
 * <pre><code class="language-java">
 * &#64;JStacheConfig(using = DropwizardJStacheConfig.class)
 * &#47;&#47; some class, package-info, module-info
 * </code>
 * </pre>
 * <strong>Step 3: have JStache models implement {@link io.jstach.opt.dropwizard.JStacheViewSupport}</strong>
 * <pre><code class="language-java">
 * &#64;JStache
 * public record ExampleModel(String message) implements JStacheViewSupport {}
 * </code>
 * </pre>
 * <strong>Step 4: return dropwizard views by using toView()</strong>
 * <pre><code class="language-java">
 * &#64;GET
 * public View hello() {
 *     return new ExampleModel("Hello world dropwizard using mixin").toView();
 * }
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