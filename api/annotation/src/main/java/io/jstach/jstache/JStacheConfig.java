package io.jstach.jstache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Set module or package level config for {@link JStache} annotated models that do not
 * have the configuration explicitly set (e.g. they have something other than the default
 * annotation return type).
 * <p>
 * The order of which settings is preferred if set is:
 * <ol>
 * <li>Class annotated with JStache.</li>
 * <li>Package annotated with this annotation set to non default.</li>
 * <li>Module annotated with this annotation set to non default.</li>
 * </ol>
 * @author agentgt
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.MODULE })
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
	 * Optional base content type for all models in the annotated package that have
	 * {@link JStache#contentType()} set to {@link JStacheContentType.AutoContentType}.
	 * The content type provider class needs a {@link JStacheContentType} annotation.
	 * @return the base content type for all models that are set to auto.
	 * @see JStacheContentType
	 */
	Class<?> contentType() default JStacheContentType.AutoContentType.class;

	/**
	 * Optional base formatter for all models in the annotated package that have
	 * {@link JStache#formatter()} set to {@link JStacheFormatter.AutoFormatter}. The
	 * formatter provider class needs a {@link JStacheFormatter} annotation.
	 * @return the base formatter for all models in the annotated package that are set to
	 * auto.
	 * @see JStacheFormatter
	 */
	Class<?> formatter() default JStacheFormatter.AutoFormatter.class;

	/**
	 * Enables zero dep generated renderer.
	 * @return false if full jstachio support
	 */
	boolean minimal() default false;

	// TODO allow compiler flags here?
	// /**
	// * Compiler flags that will be used on all models in the package or module.
	// * @apiNote The flags are not combined by set union but rather the first non empty
	// * array found (see class description).
	// * @return flags empty by default
	// */
	// Flag[] flags() default {};

}
