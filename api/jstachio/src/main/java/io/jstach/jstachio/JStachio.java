package io.jstach.jstachio;

import java.io.IOException;
import java.util.ServiceLoader;
import java.util.function.Supplier;

import io.jstach.jstache.JStache;
import io.jstach.jstachio.spi.JStachioExtension;

/**
 * Render models by using reflection to lookup generated templates as well as apply
 * filtering and fallback mechanisms.
 * <h2>Example Usage</h2> <pre><code class="language-java">
 * &#64;JStache(template = "Hello {{name}}!")
 * public record HelloWorld(String name) {}
 *
 * public static String output(String name) {
 *   //Normally you would have to use generated class HelloWorldRenderer
 *   //but this JStachio allows you to render directly
 *   //from the model.
 *   return JStachio.render(new HelloWorld(name));
 * }
 * </code> </pre> Not only is the above more convenient than using the raw generated code
 * it also allows additional custom runtime behavior like filtering as well as allows
 * easier integration with web frameworks.
 *
 * @apiNote The static <strong><code>render</code></strong> methods are convenience
 * methods that will use the ServiceLoader based JStachio which loads all extensions via
 * the {@link ServiceLoader}.
 * @see JStachioExtension
 * @see JStache
 */
public interface JStachio extends Renderer<Object> {

	/**
	 * Finds a template by using the models class if possible and then applies filtering
	 * and then finally render the model by writing to the appendable.
	 * <p>
	 * {@inheritDoc}
	 */
	void execute(Object model, Appendable appendable) throws IOException;

	/**
	 * Finds a template by using the models class if possible and then applies filtering
	 * and then finally render the model by writing to the {@link StringBuilder}.
	 * <p>
	 * {@inheritDoc}
	 */
	StringBuilder execute(Object model, StringBuilder sb);

	/**
	 * Finds a template by using the models class if possible and then applies filtering
	 * and then finally render the model to a String.
	 * <p>
	 * {@inheritDoc}
	 */
	String execute(Object model);

	/**
	 * Determines if this jstachio can render the model type (the class annotated by
	 * JStache).
	 * @param modelType the models class (<em>the one annotated with {@link JStache} and
	 * not the Templates class</em>)
	 * @return true if this jstachio can render instances of modelType
	 */
	boolean supportsType(Class<?> modelType);

	/**
	 * Executes the ServiceLoader instance of JStachio
	 * {@link #execute(Object, Appendable)}.
	 * @param model never <code>null</code>
	 * @param a appendable never <code>null</code>
	 * @throws IOException if there is an error using the appendable
	 * @see #execute(Object, Appendable)
	 */
	public static void render(Object model, Appendable a) throws IOException {
		of().execute(model, a);
	}

	/**
	 * Executes the ServiceLoader instance of JStachio
	 * {@link #execute(Object, StringBuilder)}.
	 * @param model never <code>null</code>
	 * @param a appendable never <code>null</code>
	 * @return the passed in {@link StringBuilder}
	 * @see #execute(Object, StringBuilder)
	 */
	public static StringBuilder render(Object model, StringBuilder a) {
		return of().execute(model, a);
	}

	/**
	 * Executes the ServiceLoader instance of JStachio {@link #execute(Object)}.
	 * @param model the root context model. Never <code>null</code>.
	 * @return the rendered string.
	 * @see #execute(Object)
	 */
	public static String render(Object model) {
		return of().execute(model);
	}

	/**
	 * Finds ServiceLoader based jstachio.
	 * @return service loaded jstachio
	 * @throws NullPointerException if jstachio is not found
	 */
	public static JStachio of() {
		JStachio jstachio = JStachioHolder.get();
		if (jstachio == null) {
			throw new NullPointerException("JStachio not found. This is probably a classloading issue.");
		}
		return jstachio;
	}

	/**
	 * Set the static singleton of JStachio.
	 * <p>
	 * Useful if you would like to avoid using the default ServiceLoader mechanism.
	 * @param jstachioProvider if null a NPE will be thrown.
	 */
	public static void setStaticJStachio(Supplier<JStachio> jstachioProvider) {
		if (jstachioProvider == null) {
			throw new NullPointerException("JStachio provider cannot be null");
		}
		JStachioHolder.provider = jstachioProvider;
		JStachioHolder.jstachio = null;
	}

}

final class JStachioHolder {

	static Supplier<JStachio> provider = io.jstach.jstachio.spi.JStachioResolver::defaultJStachio;

	static JStachio jstachio;

	static JStachio get() {
		JStachio j = jstachio;
		if (j == null) {
			jstachio = j = provider.get();
		}
		return j;
	}

}
