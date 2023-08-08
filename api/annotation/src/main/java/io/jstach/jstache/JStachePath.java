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
	 * @return prefix of path by default {@link #UNSPECIFIED} which will resolve to
	 * {@link #DEFAULT_PREFIX} if not set elsewhere.
	 */
	public String prefix() default UNSPECIFIED;

	/**
	 * Suffix the path. A common use case is to suffix with ".mustache".
	 * @return suffix of path by default is {@link #UNSPECIFIED} which will resolve to
	 * {@link #DEFAULT_SUFFIX} if not set elsewhere.
	 */
	public String suffix() default UNSPECIFIED;

	/**
	 * The value to mean the suffix and prefix is not set.
	 * @apiNote The value is purposely not a possible valid prefix or suffix and is not
	 * the actual default.
	 */
	public static final String UNSPECIFIED = "*";

	/**
	 * The default prefix if {@link #UNSPECIFIED}.
	 */
	public static final String DEFAULT_PREFIX = "";

	/**
	 * The default suffix if {@link #UNSPECIFIED}.
	 */
	public static final String DEFAULT_SUFFIX = "";

	/**
	 * If a JStache path is empty and template is empty and suffix is unspecified the path
	 * will be generated from the class name and suffixed with this constant.
	 */
	public static final String AUTO_SUFFIX = ".mustache";

}
