package io.jstach.jstache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;
import java.net.URL;
import java.util.ServiceLoader;

/**
 * Statically registers a formatter.
 * <p>
 * Formatters are called before escapers to resolve the output of a variable (e.g.
 * {{variable}}). Colloquially you can think of them as glorified
 * {@link Object#toString()} on objects.
 * <p>
 * A class that is annotated statically provides a formatter instead of using the
 * {@link ServiceLoader} and can be used as marker for a particular formatter in
 * {@link JStacheConfig#formatter()}.
 * <p>
 * There are two supported Formatter types:
 * <ul>
 * <li>{@code io.jstach.jstachio.Formatter}
 * <li>{@code java.util.function.Function<Object,String>}
 * </ul>
 *
 * The Function one is desirable if you would like no reference of jstachio runtime api in
 * your code base and or just an easier interface to implement.
 * <p>
 * On the otherhand the Formatter interfaces allows potentially greater performance and or
 * if you need to format native types.
 * <p>
 * It is important to understand that formatters do not have complete control what types
 * are allowed to be formatted. <strong>That is a formatter might be able to output a
 * certain class but the annotation processor will fail before that</strong> as only
 * certain types are allowed to be formatted. To control what types are allowed to be
 * formatted (and thus will use the formatter at runtime) see
 * {@link JStacheFormatterTypes} which also allows the formatter itself to be annotated.
 * <p>
 * Consequently if a class is annotated with <code>this</code> annotation and
 * {@link JStacheFormatterTypes} the types will be added to the whitelist of allowed types
 * if the annotated formatter is picked. Because you often need to whitelist types to
 * allow customer formatters it is a best practice you annotate the formatter with
 * whatever custom types are allowed.
 * <p>
 * For example let us say we want to add a custom formatter that is aware of
 * <code>LocalDate</code> we would add: <pre class="language-java">
 * <code>
 * &#64;JStacheFormatter
 * &#64;JStacheFormatterTypes(types={LocalDate.class})
 * public class MyFormatter {
 *   //required factory method
 *   public static Formatter provider() { ... }
 * }
 * </code></pre>
 *
 * <p>
 * <strong>All formatters should be able to handle:</strong>
 * <ul>
 * <li>{@link String}
 * <li>native types both unboxed or boxed
 * <li>{@link URI}
 * <li>{@link URL}
 * </ul>
 *
 * <em>(whether they output something however is up to the formatter).</em> Because the
 * allowed types can be widened more than what the formatter is annotated for a formatter
 * also needs to be prepared for that. The default formatters in JStachio will
 * <code>toString()</code> objects that are not recognized but whose types are whitelisted
 * via {@link JStacheFormatterTypes} from other config.
 *
 * <p>
 * Because formatters have to be rather omiscient of all types consider using a lambda
 * {@link JStacheLambda} for formatting complex objects.
 *
 * @apiNote <em> n.b. the class annotated does not need to implement the formatter
 * interface! It just needs to provide it.</em>
 *
 * @author agentgt
 * @see JStacheFormatterTypes
 * @see JStacheConfig#formatter()
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface JStacheFormatter {

	// TODO maybe this should be called JStacheFormatterProvider?

	/**
	 * A static method that will return an implementation of
	 * {@code io.jstach.api.runtime.Formatter} or {@code Function<Object,String> }
	 * @return default method name is <code>provider</code> just like the
	 * {@link ServiceLoader}
	 */
	String providesMethod() default "provider";

	/**
	 * A formatter type marker to resolve the formatter based on config elsewhere.
	 *
	 * @apiNote The <code>provider</code> method is purposely missing to avoid coupling
	 * with the runtime.
	 * @author agentgt
	 */
	@JStacheFormatter
	public final class UnspecifiedFormatter {

		private UnspecifiedFormatter() {
		}

	}

}
