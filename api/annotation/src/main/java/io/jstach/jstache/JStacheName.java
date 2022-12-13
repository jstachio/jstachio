package io.jstach.jstache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configures how to name the generated template java code (the classes generated from
 * JStache annotations). See {@link JStacheConfig} on how this configuration is fully
 * resolved.
 *
 * @apiNote The default return values of {@link #UNSPECIFIED} on the annotation methods
 * are not the actual default but rather signify not set.
 * @author agentgt
 * @see JStacheConfig#naming()
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.ANNOTATION_TYPE })
@Documented
public @interface JStacheName {

	/**
	 * If {@link JStache#name()} is blank the name of the generated class is derived from
	 * the models class name plus the return value if the return value is not
	 * "{@value #UNSPECIFIED}".
	 * @return suffix for generated classes.
	 * @see #DEFAULT_SUFFIX
	 */
	String prefix() default UNSPECIFIED;

	/**
	 * If {@link JStache#name()} is blank the name of the generated class is derived from
	 * the models class name plus the return value if the return value is not
	 * "{@value #UNSPECIFIED}".
	 * @return suffix for generated classes.
	 * @see #DEFAULT_SUFFIX
	 */
	String suffix() default UNSPECIFIED;

	/**
	 * The value to mean the suffix and prefix is not set.
	 * @apiNote The value is purposely not a possible valid prefix or suffix and is not
	 * the actual default.
	 */
	public static final String UNSPECIFIED = "*";

	/**
	 * The default prefix if no {@link #prefix()} is set anywhere. The generated renderers
	 * by default are prefix with this literal: <code>{@value #DEFAULT_PREFIX}</code>
	 */
	public static final String DEFAULT_PREFIX = "";

	/**
	 * The default suffix if no {@link #suffix()} is set anywhere. The generated renderers
	 * by default are suffix with this literal: <code>{@value #DEFAULT_SUFFIX}</code>
	 */
	public static final String DEFAULT_SUFFIX = "Renderer";

}
