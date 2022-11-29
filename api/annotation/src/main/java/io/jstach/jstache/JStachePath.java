package io.jstach.jstache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configure how the paths of templates map to actual source resources.
 *
 * Order of path config lookup and precedence is as follows:
 * <ol>
 * <li>type annotated with JStache and this annotation.
 * <li>package annotated with this annotation.
 * <li>module annotated with this annotation.
 * </ol>
 * If multiple annotations are found the first one is picked and there is no combining of
 * settings.
 * @author agentgt
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.MODULE, ElementType.PACKAGE, ElementType.TYPE })
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
