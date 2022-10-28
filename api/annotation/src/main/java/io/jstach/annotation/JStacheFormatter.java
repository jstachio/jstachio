package io.jstach.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ServiceLoader;

/**
 * Statically registers a formatter.
 * <p>
 * Formatters are called before escapers to resolve the output of a variable (e.g.
 * {{variable}}). Colloquially you can think of them as glorified {@link #toString()} on
 * objects.
 * <p>
 * A class that is annotated statically provides a formatter instead of using the
 * {@link ServiceLoader} and can be used as marker for a particular formatter in
 * {@link JStacheFormatterTypes#formatter()}.
 * <p>
 * There are two supported Formatter types:
 * <ul>
 * <li>{@code io.jstach.Formatter}
 * <li>{@code java.util.function.Function<Object,String>}
 * </ul>
 *
 * The Function one is desirable if you would like no reference of jstachio runtime api in
 * your code base and or just an easier interface to implement.
 * <p>
 * On the otherhand the Formatter interfaces allows potentially greater performance and or
 * if you need to format native types.
 * <p>
 * Unlike the io.jstach.JStacheServices option of creating the formatter the static
 * registration does not know about other formatters or the model.
 *
 * @apiNote <em> n.b. the class annotated does not need to implement the formatter
 * interface. It just needs to provide it.</em>
 *
 * @author agentgt
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface JStacheFormatter {

	// TODO maybe this should be called JStacheFormatterProvider?

	/**
	 * A static method that will return an implementation of {@code io.jstach.Formatter}
	 * or {@code Function<Object,String> }
	 * @return default method name is "provides" just like the {@link ServiceLoader}
	 */
	String providesMethod() default "provides";

	/**
	 * A formatter type marker to auto resolve the base formatter.
	 *
	 * @apiNote The provides method is purposely missing to avoid coupling with the
	 * runtime.
	 * @author agentgt
	 */
	@JStacheFormatter
	public final class AutoFormatter {

		private AutoFormatter() {
		}

	}

}
