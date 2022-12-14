package io.jstach.jstache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows you to define and remap/override partials on a model.
 * <p>
 * Order of config lookup and precedence is as follows:
 * <ol>
 * <li>type annotated with JStache and this annotation.
 * <li>package annotated with this annotation.
 * <li>module annotated with this annotation.
 * </ol>
 * If multiple annotations are found they are combined where registered partials with same
 * name are resolved with the above precedence.
 * @see JStachePartial
 * @see JStachePath
 * @author agentgt
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.PACKAGE, ElementType.MODULE })
@Documented
public @interface JStachePartials {

	/**
	 * Multiple partial mappings.
	 * @return multiple partial mappings
	 */
	public JStachePartial[] value() default {};

}
