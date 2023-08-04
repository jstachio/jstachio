package io.jstach.jstachio.context;

import io.jstach.jstache.JStacheConfig;
import io.jstach.jstachio.Output;

/**
 * A context aware template.
 *
 * @param <T> model type
 */
public interface ContextTemplate<T> {

	/**
	 * Renders the passed in model to an appendable like output.
	 * @param <A> output type
	 * @param <E> error type
	 * @param model a model assumed never to be <code>null</code>.
	 * @param context context node.
	 * @param appendable the appendable to write to.
	 * @return the passed in appendable for convenience
	 * @throws E if there is an error writing to the output
	 * @apiNote if the eventual output is to be bytes use
	 * {@link #write(Object, ContextNode, io.jstach.jstachio.Output.EncodedOutput)} as it
	 * will leverage pre-encoding if the template has it.
	 */
	public <A extends Output<E>, E extends Exception> A execute( //
			T model, //
			ContextNode context, //
			A appendable) throws E;

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
			ContextNode context, //
			A output) throws E;

}
