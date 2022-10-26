package io.jstach.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows you to define and remap/override partials on a model ({@link JStache}) <pre>
 *  {{> name }}
 *  </pre> Name in this case is defined by {@link #name()} and would be the logical name
 * of the partial. The physical definition of the partial is defined with a resource
 * {@link #path()} or inlined {@link #template()}.
 * <p>
 * The {@link #path()} is still expanded by {@link JStachePath} if present.
 * @author agentgt
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@Documented
public @interface JStachePartial {

	String name();

	String path() default "";

	String template() default NOT_SET;

	public static String NOT_SET = "__NOT_SET__";

}
