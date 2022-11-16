package io.jstach.jstache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows you to define and remap/override partials on a model.
 * 
 * @see JStachePartial
 * @see JStachePath
 * @author agentgt
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface JStachePartials {

	/**
	 * Multiple partial mappings.
	 * @return multiple partial mappings
	 */
	public JStachePartial[] value() default {};

}
