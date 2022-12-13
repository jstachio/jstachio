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
 * of <a href="#_unspecified">unspecified</a> annotation return type).
 *
 * <h2 id="_config_resolution">Config Resolution</h2>
 *
 * The order of which settings is preferred if set in general is
 * <code>Class > Package > Module</code>.
 * <p>
 * Specifically the resolution is:
 *
 * <ol>
 * <li>Class annotated with JStache and setting is in JStache annotation set to
 * <em>NOT</em> <a href="#_unspecified">unspecified</a>.</li>
 * <li>Class annotated with JStache and this annotation <em>NOT</em> set to
 * <a href="#_unspecified">unspecified</a>.</li>
 * <li>Package annotated with this annotation <em>NOT</em> set to
 * <a href="#_unspecified">unspecified</a>.</li>
 * <li>Module annotated with this annotation <em>NOT</em> set to
 * <a href="#_unspecified">unspecified</a>.</li>
 * <li>If everything is <a href="#_unspecified">unspecified</a> at this point the real
 * default is used (not the default return of the annotation).</li>
 * </ol>
 *
 * <em>While package hiearchy may seem natural for cascading config package hiearchy does
 * not matter to this library! Resolution will not check up parent package
 * directories!</em> If you do not want to copy config to each package it is recommended
 * you use module annotations.
 *
 * <h2 id="_unspecified">Unspecified</h2>
 *
 * Annotation methods that return symbols prefixed with "<code>Unspecified</code>" (e.g.
 * {@link JStacheType#UNSPECIFIED}) or have values called <code>UNSPECIFIED</code>
 * represent unset (they are not the actual default) and will be resolved through the
 * <a href="_config_resolution">config resolution</a>.
 *
 *
 *
 * @author agentgt
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.MODULE, ElementType.TYPE })
@Documented
public @interface JStacheConfig {

	/**
	 * If {@link JStache#adapterName()} is blank the name of the generated class is
	 * derived from the models class name plus the return value.
	 * @return by default a JStacheName that is unspecified will be returned which
	 * represents NOT SET.
	 * @see JStacheName
	 */
	JStacheName naming() default @JStacheName(suffix = JStacheName.UNSPECIFIED, prefix = JStacheName.UNSPECIFIED);

	/**
	 * Optional base content type for all models in the annotated package or module that
	 * have {@link JStache#contentType()} set to
	 * {@link JStacheContentType.UnspecifiedContentType}. The content type provider class
	 * needs a {@link JStacheContentType} annotation.
	 * @return the content type for all models that are set to unspecified.
	 * @see JStacheContentType
	 */
	Class<?> contentType() default JStacheContentType.UnspecifiedContentType.class;

	/**
	 * Optional base formatter for all models in the annotated package or module that have
	 * {@link JStache#formatter()} set to {@link JStacheFormatter.UnspecifiedFormatter}.
	 * The formatter provider class needs a {@link JStacheFormatter} annotation.
	 * @return the base formatter for all models in the annotated package that are set to
	 * auto.
	 * @see JStacheFormatter
	 */
	Class<?> formatter() default JStacheFormatter.UnspecifiedFormatter.class;

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
	 * @return {@link JStacheType#UNSPECIFIED} by default which means the generated code
	 * will depend on jstachio runtime if no other config overrides (ie is not set to
	 * auto).
	 */
	JStacheType type() default JStacheType.UNSPECIFIED;

}
