package io.jstach.jstache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.charset.StandardCharsets;

/**
 * Set module or package level config for {@link JStache} annotated models that do not
 * have the configuration explicitly set (e.g. they have something other than the default
 * annotation return type).
 * <p>
 * The order of which settings is preferred if set is:
 * <ol>
 * <li>Class annotated with JStache and setting is in JStache annotation to non
 * default</li>
 * <li>Class annotated with JStache and this annotation set to non default</li>
 * <li>Package annotated with this annotation set to non default.</li>
 * <li>Module annotated with this annotation set to non default.</li>
 * </ol>
 *
 * @apiNote Annotation methods that return symbols prefixed with "<code>Auto</code>" (e.g.
 * {@link JStacheType#AUTO}) represent unset default and will be resolved (that is the
 * auto symbol will not actually be used).
 * @author agentgt
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.MODULE, ElementType.TYPE })
@Documented
public @interface JStacheConfig {

	/**
	 * If {@link JStache#adapterName()} is blank the name of the generated class is
	 * derived from the models class name plus the return value if the return value is not
	 * blank.
	 * @return suffix for generated classes.
	 * @see JStache#IMPLEMENTATION_SUFFIX
	 */
	String nameSuffix() default "";

	/**
	 * Optional base content type for all models in the annotated package or module that
	 * have {@link JStache#contentType()} set to
	 * {@link JStacheContentType.AutoContentType}. The content type provider class needs a
	 * {@link JStacheContentType} annotation.
	 * @return the base content type for all models that are set to auto.
	 * @see JStacheContentType
	 */
	Class<?> contentType() default JStacheContentType.AutoContentType.class;

	/**
	 * Optional base formatter for all models in the annotated package or module that have
	 * {@link JStache#formatter()} set to {@link JStacheFormatter.AutoFormatter}. The
	 * formatter provider class needs a {@link JStacheFormatter} annotation.
	 * @return the base formatter for all models in the annotated package that are set to
	 * auto.
	 * @see JStacheFormatter
	 */
	Class<?> formatter() default JStacheFormatter.AutoFormatter.class;

	/**
	 * Encoding of template files.
	 * <p>
	 * charset can be omitted. <strong>If not set {@link StandardCharsets#UTF_8} is used
	 * by default.</strong>
	 * @return encoding of given template file
	 */
	String charset() default "";

	/**
	 * Determines what style of of code to generate. See {@link JStacheType}.
	 * @return {@link JStacheType#AUTO} by default which means the generated code will
	 * depend on jstachio runtime if no other config overrides (ie is not set to auto).
	 */
	JStacheType type() default JStacheType.JSTACHIO;

}
