package io.jstach.jstache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tag a method to be used as a mustache lambda section.
 *
 * Lambda sections look something like: <pre>
 * {{#context}}
 * {{#lambda}}body{{/lambda}}
 * {{/context}}
 * </pre> Where in the above example a lambda is named "lambda" and optionally has access
 * to the object called "context" and the raw body passed to the lambda is "body". A
 * nonexhaustive example of the above lambda in Java assuming the context type is
 * <code>SomeType</code> <em>might</em> look like:
 *
 * <pre><code class="language-java">
 * &#64;JStacheLambda
 * &#64;JStacheLambda.Raw
 * public String lambda(SomeType context, &#64;JStacheLambda.Raw String body) {
 *     return "Hello" + body "!";
 * }
 * </code> </pre>
 *
 * The lambda could also return a new model and use the section body as a template instead
 * of raw content:
 *
 * <pre><code class="language-hbs">
 * {{#context}}
 * {{#lambda}}{{message}}{{/lambda}}
 * {{/context}}
 * </code> </pre> <pre><code class="language-java">
 * record Model(String message){}
 *
 * &#64;JStacheLambda
 * public Model lambda(SomeType context) {
 *     return new Model("Hello " + context.name() + "!");
 * }
 * </code> </pre>
 * <p>
 * JStachio lambdas just like normal method calls do not have to be directly enclosed on
 * the context objects but can be on implemented interfaces or inherited and thus models
 * can be "mixed in" with interfaces to achieve sharing of lambdas. However there is
 * currently no support for static methods to be used as lambdas.
 * <p>
 * JStachio lambdas work in basically two modes for <strong>parameters</strong>:
 * <ol>
 * <li><strong>Context aware:</strong> The default. The top of the stack is passed if an
 * argument is present and is not annotated with {@link Raw}.
 * <li><strong>Raw:</strong> If a string parameter is annoated with {@link Raw} it will be
 * passed the contents of the lambda section call. <strong>Some caveats:</strong>
 * <ul>
 * <li><em>While this mode appears to be the default for the spec it is not for
 * JStachio.</em></li>
 * <li><em>The contents may not be valid mustache as the spec does not define that it has
 * to be.</em></li>
 * <li><em>If the lambda start tag is standalone the space and newline following the tag
 * will not be passed to the lambda.</em></li>
 * </ul>
 * </ol>
 * <p>
 * Similarly JStachio works in two modes for <strong>return types</strong>:
 * <ol>
 * <li><strong>Model: </strong> The default. The returned model is pushed onto the context
 * stack and the contents of the lambda section call are used as an inline template and
 * rendered against it.
 * <li><strong>Raw: </strong> If the return type is a {@link String} and the method is
 * annotated with {@link Raw} the contents of the string are directly written
 * <em>unescaped</em>.
 * </ol>
 * Regardless of parameter and return annotations the method must always be annotated with
 * this annotation to be discovered.
 * <p>
 * Due to the static nature of JStachio, JStachio does not support returning dynamic
 * templates which is the optional lambda spec default if a {@link String} is returned.
 * <p>
 * JStachio is very similar to the
 * <a href="https://github.com/samskivert/jmustache">JMustache</a> model and does not have
 * an analog in <a href="https://github.com/spullara/mustache.java">mustache.java</a>.
 * JStachio currently does not support returning closures of
 * {@code Function<String,String> } like mustache.java but models can be like
 * {@code Supplier<String>} for that use case.
 *
 * @see Raw
 * @see JStacheInterfaces
 * @author agentgt
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface JStacheLambda {

	/**
	 * The logical name of the lambda. If blank the method name will be used.
	 * @return lambda name
	 */
	String name() default "";

	/**
	 * Tag a method return type of String or parameter of String to be used as a raw
	 * unprocessed string.
	 * @author agentgt
	 * @see JStacheLambda
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.PARAMETER, ElementType.METHOD })
	@Documented
	public @interface Raw {

	}

}
