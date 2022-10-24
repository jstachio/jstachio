package io.jstach.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configure how the paths of templates map to actual source resources.
 *
 * If the annotation is present on the class it will take precedence over the annotation
 * in its package (package-info.java) if there is one.
 *
 * @author agent
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.TYPE })
@Documented
public @interface JStachePath {

	/**
	 * Will prefix the path. If you are mapping to a directory remember to end the prefix
	 * with a "/".
	 * @return prefix of path
	 */
	public String prefix() default "";

	/**
	 * Suffix the path. A common use case is to suffix with ".mustache".
	 * @return suffix of path
	 */
	public String suffix() default "";

}
