package io.jstach.jstache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is useful to force models and templates implement interfaces or have
 * annotations particularly where you want all models to implement a lambda mixin
 * interface. It also maybe useful to make generateed code have annotations for DI
 * frameworks (Spring, Dagger, CDI, etc) to find generated templates as components.
 * <p>
 * <strong>Example</strong>
 *
 * <pre><code class="language-java">
 * &#64;JStacheInterfaces(templateAnnotations=Component.class)
 * module com.myapp {
 *   require transitive io.jstach.jstachio;
 * }
 * </code> </pre> or for package level <pre><code class="language-java">
 * &#64;JStacheInterfaces(templateImplements=MyMixin.class)
 * package com.myapp.templates;
 * </code> </pre>
 *
 * Now all generated templates (aka renderers) that are in the <code>com.myapp</code>
 * module and <code>com.myapp.templates</code> will be generated like:
 *
 * <pre><code class="language-java">
 * &#64;Component
 * public class MyTemplate implements MyMixin &#47;*, possibly more needed for jstachio *&#47; {
 *  &#47;&#47; implementation ommitted
 * }
 * </code> </pre>
 *
 * @author agentgt
 * @see JStacheLambda
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.MODULE })
@Documented
public @interface JStacheInterfaces {

	/**
	 * Will make all generated templates that are in the annotated package implement the
	 * array of interfaces. The interfaces should have a default implementation for all of
	 * its methods otherwise compilation errors will happen.
	 * @return interfaces that generate template will implement
	 */
	public Class<?>[] templateImplements() default {};

	/**
	 * Will make all generated templates that are in the annotated package be annotated
	 * with the array of interfaces. The order is preserved in the generated code.
	 * @return annotations to be added to generate templates
	 */
	public Class<?>[] templateAnnotations() default {};

	/**
	 * Will <strong>check</strong> that all models in the annotated package annotated with
	 * {@link io.jstach.jstache.JStache} implement the array of interfaces. If a model
	 * does not a compilation error will happen.
	 * @return interfaces that the moduls <strong>should</strong> implement
	 */
	public Class<?>[] modelImplements() default {};

}
