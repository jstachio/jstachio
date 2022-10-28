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

	/**
	 * The logical name of the template.
	 * @return required name of template
	 */
	String name();

	/**
	 * The physical path of the template. If empty {@link #template()} will be used.
	 * @return the physical resource path of the template.
	 * @see JStachePath
	 */
	String path() default "";

	/**
	 * Inline template. If not set {@link #path()} will be used.
	 * @return inlined template maybe not set.
	 */
	String template() default NOT_SET;

	/**
	 * Marks that the inline template is not set
	 */
	public static String NOT_SET = "__NOT_SET__";

}
