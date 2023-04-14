package io.jstach.jstache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Place on package to generate a TemplateProvider that will have a catalog of all public
 * generated JStache templates in the compile time boundary that are of type
 * {@link JStacheType#JSTACHIO}.
 * <p>
 * The class will be put in the annotated package.
 * <p>
 * This is useful for modular applications that do not want to allow reflective access to
 * the JStachio runtime but instead can register the generated template provider in the
 * <code>module-info.java</code>.
 *
 * @author agentgt
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.PACKAGE })
@Documented
public @interface JStacheCatalog {

	/**
	 * Name of the generated class.
	 * @return name of the class to be generated. The default is
	 * <code>TemplateCatalog</code>.
	 */
	public String name() default "TemplateCatalog";

}
