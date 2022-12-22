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
 * <li>enclosing class (of type annotated with JStache) with this annotation with inner to
 * outer order.
 * <li>package annotated with this annotation.
 * <li>module annotated with this annotation.
 * </ol>
 * After this lookup is done then the lookup is repeated using
 * {@link JStacheConfig#pathing()} thus using this annotation directly on an element takes
 * precedence over {@link JStacheConfig}.
 * <p>
 * If multiple annotations are found the first one is picked and there is no combining of
 * settings. See {@link JStacheConfig} for general config resultion.
 * @author agentgt
 * @see JStacheConfig
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.MODULE, ElementType.PACKAGE, ElementType.TYPE, ElementType.ANNOTATION_TYPE })
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
