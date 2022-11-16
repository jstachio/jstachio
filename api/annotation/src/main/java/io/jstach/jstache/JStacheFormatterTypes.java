package io.jstach.jstache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;

import io.jstach.jstache.JStacheFormatter.AutoFormatter;

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
	 * Whitelist classes that will be allowed to be passed to the formatter.
	 * @return Allowed classes that will be passed to the formatter.
	 */
	public Class<?>[] types() default {};

	/**
	 * List of regex used to match whitelist class names that are allowed to be formatted.
	 * @return regex used to match class names that are allowed to be formatted.
	 */
	public String[] patterns() default {};

	/**
	 * Optional base formatter for all models in the annotated package. The formatter
	 * provider needs a {@link JStacheFormatter} annotation.
	 * @return the base formatter for all models in the annotated package
	 * @see JStacheFormatter
	 */
	public Class<?> formatter() default AutoFormatter.class;

}
