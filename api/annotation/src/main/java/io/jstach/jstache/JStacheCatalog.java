package io.jstach.jstache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ServiceLoader;

/**
 * Place on package to generate a TemplateProvider/JStachioTemplateFinder that will have a
 * catalog of all public generated JStache templates in the compile time boundary that are
 * of type {@link JStacheType#JSTACHIO}.
 * <p>
 * The class will be put in the annotated package and implements both
 * <code>io.jstach.jstachio.spi.TemplateProvider</code> and
 * <code>io.jstach.jstachio.spi.JStachioTemplateFinder</code>.
 * <p>
 * This is useful for:
 *
 * <ol>
 * <li>Modular applications that use <code>module-info.java</code> instead of
 * <code>META-INF/services</code> for service loader registration.</li>
 * <li>Application wishing to avoid reflection altogether but still wanting to use
 * JStachio runtime particularly the model to template loookup.</li>
 * <li>Allow access and rendering of package protected models from other parts of the
 * application.</li>
 * </ol>
 *
 * <h2>Modular applications</h2>
 *
 * Modular applications that do not want to allow reflective access
 * (<code> open ... to </code>) to the JStachio runtime can instead register the generated
 * template provider in the <code>module-info.java</code> as a service provider like:
 *
 * <pre><code class="language-java">
 * provides io.jstach.jstachio.spi.TemplateProvider with annotatedpackage.TemplateCatalog;
 * </code> </pre>
 *
 * or as a <code>JStachioTemplateFinder</code>:
 *
 * <pre><code class="language-java">
 * provides io.jstach.jstachio.spi.JStachioTemplateFinder with annotatedpackage.TemplateCatalog;
 * </code> </pre>
 *
 * In general the <code>TemplateProvider</code> is preferred as it will allow reflective
 * access (either {@link ServiceLoader} or direct constructor reflection) to other models
 * that are perhaps not in the same compile time boundary as well as caching. Note that
 * the generated code does not implement caching so if going the
 * <code>JStachioTemplateFinder</code> route caching will be the implementers
 * responsibility.
 *
 * <strong>Tip:</strong><em>Some tools do not like generated classes being referenced in
 * <code>module-info.java</code> therefore a general recommendation is to extend the
 * generated class and reference the class doing the extending in the
 * <code>module-info.java</code>.</em>
 *
 * <h2>Avoiding reflection</h2>
 *
 * For those wanting to avoid reflection a custom JStachio can be created from the
 * generated catalog.
 *
 * <pre><code class="language-java">
 * JStachio j = JStachioFactory.builder().add(new annotatedpackage.TemplateCatalog()).build();
 * </code> </pre>
 *
 * <h2>Package protected models</h2>
 *
 * If the package annotated has models that are package protected those models will still
 * be added to the generated template catalog as the generated class will have access.
 * Normally JStachio uses either the Service Loader which requires public access to the
 * generated template or reflection which may or may not require public access depending
 * on modularization.
 * <p>
 * <em>Multiple packages can be annotated and thus multiple TemplateProvider/Finder can be
 * referenced.</em>
 *
 *
 * @author agentgt
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.PACKAGE })
@Documented
public @interface JStacheCatalog {

	/**
	 * Name of the generated class that will be put in the annotated package.
	 * @return name of the class to be generated. The default is
	 * <code>TemplateCatalog</code>.
	 */
	public String name() default "TemplateCatalog";

	/**
	 * Configuration flags for generating template catalogs.
	 * @return an array of flags.
	 * @see CatalogFlag
	 */
	public CatalogFlag[] flags() default {};

	/**
	 * Configuration flags for generating template catalogs such
	 * <code>META-INF/services</code> files.
	 * @author agentgt
	 */
	public enum CatalogFlag {

		/**
		 * Option that will generate a legacy service loader registration:
		 * <code>META-INF/services/io.jstach.jstachio.spi.TemplateProvider</code> pointing
		 * to the generated template catalog.
		 */

		GENERATE_PROVIDER_META_INF_SERVICE, //

		/**
		 * Option that will generate a legacy service loader registration:
		 * <code>META-INF/services/io.jstach.jstachio.spi.JStachioTemplateFinder</code>
		 * pointing to the generated template catalog.
		 */
		GENERATE_FINDER_META_INF_SERVICE

	}

}
