package io.jstach.jstachio;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.function.Supplier;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheConfig;
import io.jstach.jstachio.spi.AbstractJStachio;
import io.jstach.jstachio.spi.JStachioConfig;
import io.jstach.jstachio.spi.JStachioExtension;
import io.jstach.jstachio.spi.JStachioFactory;

/**
 * Render models by using reflection or static catalog to lookup generated templates as
 * well as apply filtering and fallback mechanisms.
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
 * <h2>Customize</h2>
 *
 * The default {@link JStachio} uses the {@link ServiceLoader} to load
 * {@link JStachioExtension}s. You can customize it by adding jars that have provided
 * {@link JStachioExtension}s or by {@linkplain JStachioConfig adjusting config}.
 *
 * <p>
 *
 * If you would like to create your own {@link JStachio} instead of the default you can
 * either extend {@link AbstractJStachio} or use {@link JStachioFactory#builder()}. If you
 * want your custom {@link JStachio} to be set as the default such that the static render
 * methods on this class call it you can do that with {@link #setStatic(Supplier)}.
 *
 * @apiNote The static <strong><code>render</code></strong> methods are convenience
 * methods that will by default use the ServiceLoader based JStachio which loads all
 * extensions via the {@link ServiceLoader}.
 * @see JStachioExtension
 * @see JStache
 * @see JStachioFactory#builder()
 */
public interface JStachio extends Renderer<Object> {

	/**
	 * Finds a template by using the models class if possible and then applies filtering
	 * and then finally render the model by writing to the appendable.
	 * <p>
	 * {@inheritDoc}
	 */
	default void execute(Object model, Appendable appendable) throws IOException {
		Renderer.super.execute(model, appendable);
	}

	/**
	 * Finds a template by using the models class if possible and then applies filtering
	 * and then finally render the model by writing to the {@link StringBuilder}.
	 * <p>
	 * {@inheritDoc}
	 */
	default StringBuilder execute(Object model, StringBuilder sb) {
		return Renderer.super.execute(model, sb);
	}

	/**
	 * Finds a template by using the models class if possible and then applies filtering
	 * and then finally render the model to a String.
	 * <p>
	 * {@inheritDoc}
	 */
	default String execute(Object model) {
		return Renderer.super.execute(model);
	}

	/**
	 * Renders the passed in model directly to a binary stream possibly leveraging
	 * pre-encoded parts of the template. This <em>may</em> improve performance when
	 * rendering UTF-8 to an OutputStream as some of the encoding is done in advance.
	 * Because the encoding is done statically you cannot pass the charset in. The chosen
	 * charset comes from {@link JStacheConfig#charset()}.
	 * @param <A> output type
	 * @param <E> error type
	 * @param model a model assumed never to be <code>null</code>.
	 * @param output to write to.
	 * @return the passed in output for convenience
	 * @throws UnsupportedCharsetException if the encoding of the output does not match
	 * the template.
	 * @throws E if an error occurs while writing to output
	 */
	public <A extends io.jstach.jstachio.Output.EncodedOutput<E>, E extends Exception> A write( //
			Object model, //
			A output) throws E;

	/**
	 * Finds a template by model. This is useful if you need metadata before writing such
	 * as charset and media type for HTTP output which the template has.
	 * <p>
	 * The returned template is decorated if filtering is on and a filter that is not the
	 * template is applied.
	 * <p>
	 * Passing in a {@link TemplateModel} should work as well and the returned template
	 * will be able to execute the TemplateModel as though it were a regular model.
	 * @param model the actual model or a {@link TemplateModel} containing the model
	 * @return a filtered template
	 * @throws NoSuchElementException if a template is not found and no other lookup
	 * errors happen.
	 * @throws Exception if template cannot be found for unexpected reasons such as
	 * reflection errors.
	 * @apiNote implementations should handle {@link TemplateModel} passed in.
	 */
	Template<Object> findTemplate(Object model) throws Exception;

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
	 * Gets the static singleton jstachio.
	 * @return the jstachio from {@link #setStatic(Supplier)}
	 * @throws NullPointerException if jstachio is not found
	 * @see #setStatic(Supplier)
	 */
	public static JStachio of() {
		JStachio jstachio = JStachioHolder.get();
		if (jstachio == null) {
			throw new NullPointerException("JStachio not found. This is probably a classloading issue.");
		}
		return jstachio;
	}

	/**
	 * Gets default singleton ServiceLoader based jstachio.
	 * @return service loaded jstachio
	 */
	public static JStachio defaults() {
		return io.jstach.jstachio.spi.JStachioFactory.defaultJStachio();
	}

	/**
	 * Set the static singleton of JStachio.
	 * <p>
	 * Useful if you would like to avoid using the default ServiceLoader mechanism.
	 * @param jstachioProvider if null a NPE will be thrown.
	 * @apiNote the provider will be called on every call of {@link #of()} and thus to
	 * avoid constant recreation it is recommend the supplier be memoized/cached.
	 */
	public static void setStatic(Supplier<JStachio> jstachioProvider) {
		if (jstachioProvider == null) {
			throw new NullPointerException("JStachio provider cannot be null");
		}
		JStachioHolder.provider = jstachioProvider;
	}

}

final class JStachioHolder {

	static Supplier<JStachio> provider = JStachio::defaults;
	
	private JStachioHolder() {
	}

	static JStachio get() {
		return provider.get();
	}

}
