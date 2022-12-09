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
 * Order of config lookup and precedence is as follows:
 * <ol>
 * <li>type annotated with JStache and this annotation.
 * <li>package annotated with this annotation.
 * <li>module annotated with this annotation.
 * </ol>
 * If multiple annotations are found the first one is picked and there is no combining of
 * settings.
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
 * @apiNote N.B. the rention policy is SOURCE hence why it is not in JStacheConfig.
 * @author agentgt
 * @see JStacheLambda
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE, ElementType.PACKAGE, ElementType.MODULE })
@Documented
public @interface JStacheInterfaces {

	/**
	 * Will make all generated templates that are in the annotated class/package/module
	 * implement the array of interfaces. If the interface has a single generic type
	 * parameter (e.g. {@code SomeInterface<T>}) then the parameter will be assumed to be
	 * the model type (the class annotated with JStache) and thus will be parameterized
	 * with the model type (e.g.
	 * {@code SomeModelRenderer implements SomeInterface<SomeModel>}).
	 * <p>
	 * <strong>The interfaces should have a default implementation for all of its methods
	 * otherwise possible compilation errors might happen. </strong>
	 * @return interfaces that generated template will implement
	 */
	public Class<?>[] templateImplements() default {};

	/**
	 * Will make all generated templates that are in the annotated class/package/module
	 * extened a class. If the class has a single generic type parameter (e.g.
	 * {@code SomeClass<T>}) then the parameter will be assumed to be the model type (the
	 * class annotated with JStache) and thus will be parameterized with the model type
	 * (e.g. {@code SomeModelRenderer extends SomeClass<SomeModel>}).
	 * <p>
	 * <strong> The class needs a no arg default constructor but may provide other
	 * constructors that will simply be replicated (including annotations) on the
	 * genererated template. </strong>
	 * @return interfaces that generated template will implement
	 */
	public Class<?> templateExtends() default Object.class;

	/**
	 * Will make all generated templates that are in the annotated class/package/module be
	 * annotated with the array of annotations. The order is preserved in the generated
	 * code.
	 * @return annotations to be added to generate templates
	 */
	public Class<?>[] templateAnnotations() default {};

	/**
	 * Will make all generated templates that are in the annotated class/package/module
	 * have their constructor that takes a io.jstach.jstachio.TemplateConfig annotated
	 * with the array of annotations. The order is preserved in the generated code.
	 * <p>
	 * This is useful for DI frameworks and a common pattern is to use
	 * <code>jakarta.inject.Inject.class</code> which will make the DI framework
	 * instantiate the template with a common config.
	 * @return annotations to be added to generated templates
	 * @deprecated templateExtends is preferred
	 */
	public Class<?>[] templateConstructorAnnotations() default {};

	/**
	 * Will <strong>check</strong> that all models in the annotated class/package/module
	 * annotated with {@link io.jstach.jstache.JStache} implement the array of interfaces.
	 * If a model does not a compilation error will happen.
	 * @return interfaces that the models <strong>should</strong> implement
	 */
	public Class<?>[] modelImplements() default {};

}
