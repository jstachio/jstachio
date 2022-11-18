package io.jstach.jstachio;

import java.io.IOException;
import java.util.ServiceLoader;

import io.jstach.jstache.JStache;
import io.jstach.jstachio.spi.JStacheServices;

/**
 * Render models by using reflection to lookup renderers as well as apply filtering and
 * fallback mechanisms.
 * <p>
 * The static <strong><code>render</code></strong> methods are convenience methods that
 * will use the ServiceLoader based JStachio which loads all extensions via the
 * {@link ServiceLoader}.
 *
 * @see JStacheServices
 * @see JStache
 */
public interface JStachio extends Renderer<Object> {

	/**
	 * Finds a template by using the models class if possible and then applies filtering
	 * and then finally render the model by writing to the appendable.
	 * <p>
	 * {@inheritDoc}
	 */
	public void execute(Object model, Appendable appendable) throws IOException;

	/**
	 * Finds a template by using the models class if possible and then applies filtering
	 * and then finally render the model by writing to the {@link StringBuilder}.
	 * <p>
	 * {@inheritDoc}
	 */
	public StringBuilder execute(Object model, StringBuilder sb);

	/**
	 * Finds a template by using the models class if possible and then applies filtering
	 * and then finally render the model to a String.
	 * <p>
	 * {@inheritDoc}
	 */
	public String execute(Object model);

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
		JStachio jstachio = JStacheServices.find().provideJStachio();
		if (jstachio == null) {
			throw new NullPointerException("JStachio not found. This is probably a classloading issue.");
		}
		return jstachio;
	}

}
