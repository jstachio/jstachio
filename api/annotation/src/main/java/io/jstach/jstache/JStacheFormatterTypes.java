package io.jstach.jstache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;

/**
 * Statically sets allowed formatting types.
 * <p>
 * If a type is not allowed or known a compile error will happen. This annotation allows
 * you to change that behavior by adding types.
 * <p>
 * By default the only allowed (and always allowed) types to be formatted are:
 * <ul>
 * <li>{@link String}
 * <li>native types both unboxed or boxed
 * <li>{@link URI}
 * </ul>
 * <p>
 * Order of config lookup and precedence is as follows:
 * <ol>
 * <li>type annotated with JStache and this annotation.
 * <li>enclosing class (of type annotated with JStache) with this annotation with inner to
 * outer order.
 * <li>package annotated with this annotation.
 * <li>module annotated with this annotation.
 * </ol>
 * If multiple annotations are found the first one is picked and there is no combining of
 * settings.
 * @apiNote n.b. the retention policy is SOURCE as this settings are only needed for the
 * compiler and why it is not in {@link JStacheConfig}.
 * @author agentgt
 * @see JStacheFormatter
 * @see JStacheConfig
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE, ElementType.PACKAGE, ElementType.MODULE })
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

}
