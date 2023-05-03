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
 * <li>Class annotated with JStache and setting is in JStache annotation <em>NOT</em> set
 * to <a href="#_unspecified">UNSPECIFIED</a>.</li>
 * <li>Class annotated with JStache and this annotation <em>NOT</em> set to
 * <a href="#_unspecified">UNSPECIFIED</a>.</li>
 * <li>{@link Class#getEnclosingClass() Enclosing classes} enclosing the class annotated
 * with JStache with inner to outer order annotated with this annotation <em>NOT</em> set
 * to <a href="#_unspecified">UNSPECIFIED</a>.</li>
 * <li>Package annotated with this annotation <em>NOT</em> set to
 * <a href="#_unspecified">UNSPECIFIED</a>.</li>
 * <li>Module annotated with this annotation <em>NOT</em> set to
 * <a href="#_unspecified">UNSPECIFIED</a>.</li>
 * <li>If everything is <a href="#_unspecified">unspecified</a> at this point the real
 * default is used (not the default return of the annotation).</li>
 * </ol>
 *
 * <em>While package hierarchy may seem natural for cascading config this library does not
 * do it. Package hierarchy does NOT matter to this library! Resolution will NOT check up
 * parent package directories.</em> If you do not want to copy config to each package it
 * is recommended you use module annotations or use {@link #using()} to reference other
 * configuration (see next section).
 *
 * <h2 id="_config_using">Config Importing</h2>
 *
 * You may import config annotated elsewhere with {@link #using()}. When config is
 * imported from another class it is essentially a union of NON
 * <a href="#_unspecified">UNSPECIFIED</a> settings of this annotation instance and the
 * imported class. Thus <a href="#_config_resolution">config resolution</a> follows as
 * though the settings are on this instance.
 * <p>
 * The referenced config class can be any declared type (interface, enum, etc) but it is
 * best practice to make it something like an empty Enum to avoid confusing it with
 * models.
 *
 * <h2 id="_unspecified">Unspecified</h2>
 *
 * Annotation methods that return symbols prefixed with "<code>Unspecified</code>" (e.g.
 * {@link JStacheType#UNSPECIFIED}) or have values called <code>UNSPECIFIED</code> or
 * return an empty array, empty string, or void.class represent unset and will be resolved
 * through the <a href="#_config_resolution">config resolution</a>. Consequently unlike
 * other annotation implementations the <code>default</code> return of the annotation is
 * not really the concrete default.
 *
 *
 * @apiNote This annotation and thus configuration is available during runtime unlike many
 * of the other annotations in jstachio.
 * @author agentgt
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.MODULE, ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Documented
public @interface JStacheConfig {

	/**
	 * Imports configuration from another class annotated by <strong>this</strong> or
	 * other JStache annotations. This allows config sharing even across compile
	 * boundaries.
	 *
	 * <p>
	 * The configuration on this annotation instance (the one calling {@link #using()})
	 * will however take precedence if is not <a href="_unspecified">UNSPECIFIED</a>.
	 * <p>
	 * <em><code>using()</code> is not cascading meaning that the referenced configuration
	 * classes (the using returned on this instance) that have {@link #using()} set will
	 * be ignored!</em> Furthermore configuration set on enclosing classes, packages,
	 * modules on the referenced type are ignored.
	 * @return by default <code>void.class</code> which represents
	 * <a href="_unspecified">UNSPECIFIED</a>.
	 */
	Class<?> using() default void.class;

	/**
	 * If {@link JStache#name()} is blank the name of the generated class is derived from
	 * the models class name and then augmented with {@link JStacheName#prefix()} and
	 * {@link JStacheName#suffix()}.
	 * @return by default an empty array which represents
	 * <a href="_unspecified">UNSPECIFIED</a>.
	 * @apiNote the cardinality of the returned array is currently <code>0..1</code>.
	 * additional elements after the first will be ignored.
	 * @see JStacheName
	 */
	JStacheName[] naming() default {};

	/**
	 * If {@link JStache#path()} is not blank the path of the template is resolved by
	 * augmenting with {@link JStachePath#prefix()} and {@link JStachePath#suffix()}.
	 * @return by default an empty array which represents
	 * <a href="_unspecified">UNSPECIFIED</a>.
	 * @apiNote the cardinality of the returned array is currently <code>0..1</code>.
	 * additional elements after the first will be ignored.
	 * @see JStachePath
	 */
	JStachePath[] pathing() default {};

	/**
	 * Configures what interfaces/annotations the model implements and or extends.
	 * @return by default an empty array which represents
	 * <a href="_unspecified">UNSPECIFIED</a>.
	 * @apiNote the cardinality of the returned array is currently <code>0..1</code>.
	 * additional elements after the first will be ignored.
	 * @see JStacheInterfaces
	 */
	JStacheInterfaces[] interfacing() default {};

	/**
	 * Optional content type for all models in the <a href="#_config_resolution">annotated
	 * class/package/module</a>.
	 * <p>
	 * The content type provider class needs a {@link JStacheContentType} annotation on
	 * the type. If {@link #type()} is resolved to {@link JStacheType#JSTACHIO} a spec
	 * based HTML content type will be used.
	 * @return by default an <a href="#_unspecified">UNSPECIFIED</a> content type.
	 * @see JStacheContentType
	 */
	Class<?> contentType() default JStacheContentType.UnspecifiedContentType.class;

	/**
	 * Optional formatter if not <a href="#_unspecified">UNSPECIFIED</a> for all models in
	 * the <a href="#_config_resolution">annotated class/package/module</a> .
	 * <p>
	 * The formatter provider class needs a {@link JStacheFormatter} annotation on the
	 * type. If {@link #type()} is resolved to {@link JStacheType#JSTACHIO} a default
	 * formatter that does not allow null will be used.
	 * @return by default an <a href="#_unspecified">UNSPECIFIED</a> formatter.
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
