/**
 * JStachio Core Runtime API: 
 * <a href="https://repo1.maven.org/maven2/io/jstach/jstachio/maven-metadata.xml" class="gav">io.jstach:jstachio</a>.
 * <p>
 * The main entry point is {@link io.jstach.jstachio.JStachio} 
 * which provides various reflection based lookup
 * mechanisms of models which can be useful if you do not want direct references to generated
 * code in your code base as well as applies runtime logic like filtering.
 * <p>
 * This module also provides runtime extension points via the {@link java.util.ServiceLoader} and
 * the SPI of {@link io.jstach.jstachio.spi.JStachioExtension}
 * <p>
 * By {@link io.jstach.jstache.JStacheType#JSTACHIO default JStachio generates code} that needs this module however libraries that
 * use models (classes annotated with {@link io.jstach.jstache.JStache}) 
 * without needing rendering do not necessarily need this module.
 * <p>
 * Also if all {@link io.jstach.jstache.JStacheType#STACHE JStaches are configured for zero dependency mode}
 * then this module is <em>not needed</em>.
 * <p>
 * If your application is modular you <em>might</em> need to open your model packages to this module
 * if you plan on using {@link io.jstach.jstachio.JStachio} convenience render methods.
 * <pre>
 * <code class="language-java">
 * opens your.package.with.models to io.jstach.jstachio;
 * </code>
 * </pre>
 * 
 * @see io.jstach.jstachio.JStachio
 * @jstachioVersion
 */
module io.jstach.jstachio {
	exports io.jstach.jstachio;
	exports io.jstach.jstachio.spi;
	exports io.jstach.jstachio.context;
	exports io.jstach.jstachio.escapers;
	exports io.jstach.jstachio.formatters;


	requires transitive io.jstach.jstache;
	
	requires static org.eclipse.jdt.annotation;
	
	uses io.jstach.jstachio.spi.JStachioExtension;
	uses io.jstach.jstachio.spi.TemplateProvider;
}