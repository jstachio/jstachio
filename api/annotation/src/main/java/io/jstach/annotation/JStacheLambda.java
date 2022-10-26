package io.jstach.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tag a method to be used as a mustache lambda section.
 * 
 * Lambda sections look something like:
 * <pre>
 * {{#context}}
 * {{#lambda}}body{{/lambda}}
 * {{/context}}
 * </pre>
 * Where in the above example a lambda is named "lambda" and optionally has access to the object called "context"
 * and the raw body passed to the lambda is "body".
 * <p>
 * JStachio lambdas just like normal method calls do not have to be directly enclosed on the context objects but
 * can be on implemented interfaces or inherited and thus models can be "mixed in" with interfaces to achieve sharing
 * of lambdas. However there is currently no support for static methods to be used as lambdas.
 * <p>
 * JStachio lambdas work in basically two modes for <strong>parameters</strong>:
 * <ul>
 * <li><strong>Context aware:</strong> The default. The top of the stack is
 * passed if an argument is present and is not annotated with {@link Raw}.
 * <li><strong>Raw:</strong> If a string parameter is annoated with {@link Raw}
 * it will be passed the contents of the lambda section call. <em>While this
 * mode is the default for the spec it is not for JStachio!</em>. <em>n.b. the
 * contents may not be valid mustache as the spec does not define that it has to
 * be.</em>
 * </ul>
 * 
 * Similarly JStachio works in two modes for <strong>return types</strong>:
 * <p>
 * <ul>
 * <li><strong>Model: </strong> The default. The returned model forms its own
 * isolated context stack and the contents of the lambda section call are used
 * as an inline template and rendered against the returned context.
 * <li><strong>Raw: </strong> If the return type is a {@link String} and the
 * method is annotated with {@link Raw} the contents of the string are directly
 * written <em>unescaped</em>.
 * </ul>
 * <p>
 * Due to the static nature of JStachio, JStachio does not support returning
 * dynamic templates which is the optional lambda spec default if a
 * {@link String} is returned.
 * <p>
 * JStachio is very similar to the JMustache model and does not have an analog in mustache.java.
 * JStachio currently does not support returning closures of {@code Function<String,String> }
 * like mustache.java but models can be like {@code Supplier<String>} for that use case.
 * 
 * @see Raw
 * @author agentgt
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface JStacheLambda {

	String name() default "";

	/**
	 * Tag a method return type of String or parameter of String to be used as a raw unprocessed string.
	 * @author agentgt
	 * @see JStacheLambda
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.PARAMETER, ElementType.METHOD })
	@Documented
	public @interface Raw {

	}

}
