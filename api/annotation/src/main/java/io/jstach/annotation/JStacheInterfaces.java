package io.jstach.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is useful to force models and renderers implement interfaces
 * particularly where you want all models to implement a lambda mixin interface.
 *
 * @author agentgt
 * @see JStacheLambda
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
@Documented
public @interface JStacheInterfaces {

	/**
	 * Will make all renderers that are in the annotated package implement the array of
	 * interfaces. The interfaces should have a default implementation for all of its
	 * methods otherwise compilation errors will happen.
	 * @return interfaces
	 */
	public Class<?>[] rendererImplements() default {};

	/**
	 * Will <strong>check</strong> that all models in the annotated package annotated with
	 * {@link io.jstach.annotation.JStache} implement the array of interfaces. If a model
	 * does not a compilation error will happen.
	 * @return interfaces
	 */
	public Class<?>[] modelImplements() default {};

}
