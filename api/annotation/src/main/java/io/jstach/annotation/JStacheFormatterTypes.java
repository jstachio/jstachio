package io.jstach.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;

import io.jstach.annotation.JStacheFormatter.AutoFormatter;

/**
 *
 * Statically sets allowed formatting types as well as registers static formatters for all
 * models ({@link JStache}) in a package.
 * <p>
 * If a type is not allowed or known a compile error will happen. This annotation allows
 * you to override that behavior.
 * <p>
 * By default the only allowed types to be formatted are:
 * <ul>
 * <li>{@link String}
 * <li>native types both unboxed or boxed
 * <li>{@link URI}
 * </ul>
 * @author agentgt
 * @see JStacheFormatter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
@Documented
public @interface JStacheFormatterTypes {

	/**
	 * @return Allowed classes that will be passed to the formatter.
	 */
	public Class<?>[] types() default {};

	/**
	 * @return regex used to match class names that are allowed to be formatted.
	 */
	public String[] patterns() default {};

	/**
	 * @return the base formatter for all models in the annotated package
	 */
	public Class<?> formatter() default AutoFormatter.class;

}
