package io.jstach.jstachio.context;

import io.jstach.jstache.JStacheConfig;

/**
 * A context aware template.
 *
 * @param <T> model type
 */
public interface ContextTemplate<T> {

	/**
	 * Renders the passed in model directly to a binary stream leveraging pre-encoded
	 * parts of the template. This <em>may</em> improve performance when rendering UTF-8
	 * to an OutputStream as some of the encoding is done in advance. Because the encoding
	 * is done statically you cannot pass the charset in. The chosen charset comes from
	 * {@link JStacheConfig#charset()}.
	 * @param <A> output type
	 * @param <E> error type
	 * @param model a model assumed never to be <code>null</code>.
	 * @param context context node.
	 * @param output to write to.
	 * @return the passed in output for convenience
	 * @throws E if an error occurs while writing to output
	 */
	public <A extends io.jstach.jstachio.Output.EncodedOutput<E>, E extends Exception> A write( //
			T model, //
			ContextNode context, A output) throws E;

}
