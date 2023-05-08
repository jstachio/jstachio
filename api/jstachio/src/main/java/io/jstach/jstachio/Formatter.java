package io.jstach.jstachio;

import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheFormatter;
import io.jstach.jstache.JStacheFormatterTypes;

/**
 * Formats and then sends the results to the downstream appender.
 *
 * Implementations should be singleton like and should not contain state. By default
 * native types are passed straight through to the downstream appender. If this is not
 * desired one can override those methods.
 * <p>
 * <em>Important: the formatter does not decide what types are allowed at compile time to
 * be formatted.</em> To control what types are allowed to be formatted see
 * {@link JStacheFormatterTypes}.
 * <p>
 * An alternative to implementing this complicated interface is to simply make a
 * {@code Function<@Nullable Object, String>} and call {@link #of(Function)} to create a
 * formatter.
 * <p>
 * To implement a custom formatter:
 *
 * <ol>
 * <li>Implement this interface or use {@link #of(Function)}.</li>
 * <li>Register the custom formatter with {@link JStacheFormatter}.</li>
 * <li>Add additional allowed types with {@link JStacheFormatterTypes} on to the class
 * that is annotated with {@link JStacheFormatter}</li>
 * <li>Set {@link JStacheConfig#formatter()} to the class that has the
 * {@link JStacheFormatter}.</li>
 * </ol>
 *
 * @apiNote Although the formatter has access to the raw {@link Appendable} the formatter
 * should never use it directly and simply pass it on to the downstream appender.
 * @author agentgt
 * @see JStacheFormatterTypes
 * @see JStacheFormatter
 *
 */
public interface Formatter extends Function<@Nullable Object, String> {

	/**
	 * Formats an object by using {@link StringBuilder} and calling
	 * {@link #format(Appender, Output, String, Class, Object)}.
	 * @param t the object to be formatted. Maybe <code>null</code>.
	 * @return the formatted results as a String.
	 */
	@Override
	default String apply(@Nullable Object t) {
		var sb = new Output.StringOutput(new StringBuilder());
		format(Appender.defaultAppender(), sb, "", Object.class, t);
		return sb.toString();
	}

	/**
	 * Formats the object and then sends the results to the downstream appender.
	 *
	 *
	 * @apiNote Although the formatter has access to the raw {@link Appendable} the
	 * formatter should never use it directly and simply pass it on to the downstream
	 * appender.
	 * @param <A> the appendable type
	 * @param <E> the appender exception type
	 * @param downstream the downstream appender to be used instead of the appendable
	 * directly
	 * @param a the appendable to be passed to the appender
	 * @param path the dotted mustache like path
	 * @param c the object class but is not guaranteed to be accurate. If it is not known
	 * Object.class will be used.
	 * @param o the object which maybe null
	 * @throws E if the appender or appendable throws an exception
	 */

	<A extends Output<E>, E extends Exception> void format(Appender downstream, A a, String path, Class<?> c,
			@Nullable Object o) throws E;

	/**
	 * Formats the object and then sends the results to the downstream appender. The
	 * default implementation passes natives through to the downstream appender.
	 *
	 * @apiNote Although the formatter has access to the raw {@link Appendable} the
	 * formatter should never use it directly and simply pass it on to the downstream
	 * appender.
	 * @param <A> the appendable type
	 * @param <E> the appender exception type
	 * @param downstream the downstream appender to be used instead of the appendable
	 * directly
	 * @param a the appendable to be passed to the appender
	 * @param path the dotted mustache like path
	 * @param c character
	 * @throws E if the appender or appendable throws an exception
	 */
	default <A extends Output<E>, E extends Exception> void format(Appender downstream, A a, String path, char c)
			throws E {
		downstream.append(a, c);
	}

	/**
	 * Formats the object and then sends the results to the downstream appender. The
	 * default implementation passes natives through to the downstream appender.
	 *
	 * @apiNote Although the formatter has access to the raw {@link Appendable} the
	 * formatter should never use it directly and simply pass it on to the downstream
	 * appender.
	 * @param <A> the appendable type
	 * @param <E> the appender exception type
	 * @param downstream the downstream appender to be used instead of the appendable
	 * directly
	 * @param a the appendable to be passed to the appender
	 * @param path the dotted mustache like path
	 * @param s short
	 * @throws E if the appender or appendable throws an exception
	 */
	default <A extends Output<E>, E extends Exception> void format(Appender downstream, A a, String path, short s)
			throws E {
		downstream.append(a, s);
	}

	/**
	 * Formats the object and then sends the results to the downstream appender. The
	 * default implementation passes natives through to the downstream appender.
	 *
	 * @apiNote Although the formatter has access to the raw {@link Appendable} the
	 * formatter should never use it directly and simply pass it on to the downstream
	 * appender.
	 * @param <A> the appendable type
	 * @param <E> the appender exception type
	 * @param downstream the downstream appender to be used instead of the appendable
	 * directly
	 * @param a the appendable to be passed to the appender
	 * @param path the dotted mustache like path
	 * @param i integer
	 * @throws E if the appender or appendable throws an exception
	 */
	default <A extends Output<E>, E extends Exception> void format(Appender downstream, A a, String path, int i)
			throws E {
		downstream.append(a, i);
	}

	/**
	 * Formats the object and then sends the results to the downstream appender. The
	 * default implementation passes natives through to the downstream appender.
	 *
	 * @apiNote Although the formatter has access to the raw {@link Appendable} the
	 * formatter should never use it directly and simply pass it on to the downstream
	 * appender.
	 * @param <A> the appendable type
	 * @param <E> the appender exception type
	 * @param downstream the downstream appender to be used instead of the appendable
	 * directly
	 * @param a the appendable to be passed to the appender
	 * @param path the dotted mustache like path
	 * @param l long
	 * @throws E if the appender or appendable throws an exception
	 */
	default <A extends Output<E>, E extends Exception> void format(Appender downstream, A a, String path, long l)
			throws E {
		downstream.append(a, l);
	}

	/**
	 * Formats the object and then sends the results to the downstream appender. The
	 * default implementation passes natives through to the downstream appender.
	 *
	 * @apiNote Although the formatter has access to the raw {@link Appendable} the
	 * formatter should never use it directly and simply pass it on to the downstream
	 * appender.
	 * @param <A> the appendable type
	 * @param <E> the appender exception type
	 * @param downstream the downstream appender to be used instead of the appendable
	 * directly
	 * @param a the appendable to be passed to the appender
	 * @param path the dotted mustache like path
	 * @param d double
	 * @throws E if the appender or appendable throws an exception
	 */
	default <A extends Output<E>, E extends Exception> void format(Appender downstream, A a, String path, double d)
			throws E {
		downstream.append(a, d);
	}

	/**
	 * Formats the object and then sends the results to the downstream appender. The
	 * default implementation passes natives through to the downstream appender.
	 *
	 * @apiNote Although the formatter has access to the raw {@link Appendable} the
	 * formatter should never use it directly and simply pass it on to the downstream
	 * appender.
	 * @param <A> the appendable type
	 * @param <E> the appender exception type
	 * @param downstream the downstream appender to be used instead of the appendable
	 * directly
	 * @param a the appendable to be passed to the appender
	 * @param path the dotted mustache like path
	 * @param b boolean
	 * @throws E if the appender or appendable throws an exception
	 */
	default <A extends Output<E>, E extends Exception> void format(Appender downstream, A a, String path, boolean b)
			throws E {
		downstream.append(a, b);
	}

	/**
	 * Formats the object and then sends the results to the downstream appender. The
	 * default implementation passes natives through to the downstream appender.
	 *
	 * @apiNote Although the formatter has access to the raw {@link Appendable} the
	 * formatter should never use it directly and simply pass it on to the downstream
	 * appender.
	 * @param <A> the appendable type
	 * @param <E> the appender exception type
	 * @param downstream the downstream appender to be used instead of the appendable
	 * directly
	 * @param a the appendable to be passed to the appender
	 * @param path the dotted mustache like path
	 * @param s String
	 * @throws E if the appender or appendable throws an exception
	 */
	default <A extends Output<E>, E extends Exception> void format(Appender downstream, A a, String path, String s)
			throws E {
		format(downstream, a, path, String.class, s);
	}

	/**
	 * Adapts a function to a formatter.
	 *
	 * If the function is already a formatter then it is simply returned (noop). Thus it
	 * is safe to repeatedly call this on formatters. If the function is adapted the
	 * returned adapted formatter does not pass native types to the inputted function.
	 * @param formatterFunction if it is already an escaper
	 * @return adapted formattter
	 */
	public static Formatter of(@SuppressWarnings("exports") Function<@Nullable Object, String> formatterFunction) {
		if (formatterFunction instanceof Formatter f) {
			return f;
		}
		return new ObjectFunctionFormatter(formatterFunction);
	}

}

class ObjectFunctionFormatter implements Formatter {

	private final Function<@Nullable Object, String> function;

	public ObjectFunctionFormatter(Function<@Nullable Object, String> function) {
		super();
		this.function = function;
	}

	@Override
	public <A extends Output<E>, E extends Exception> void format(Appender downstream, A a, String path, Class<?> c,
			@Nullable Object o) throws E {
		String result = function.apply(o);
		downstream.append(a, result);
	}

}
