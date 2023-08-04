package io.jstach.jstachio.context;

import io.jstach.jstache.JStacheConfig;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Output;

/**
 * See {@link JStachio}. A special JStachio that can render models with a loose typed
 * context object bound to {@value ContextNode#CONTEXT_BINDING_NAME}.
 */
public interface ContextJStachio {

	/**
	 * Renders the passed in model.
	 * @param <A> output type
	 * @param <E> error type
	 * @param model a model assumed never to be <code>null</code>.
	 * @param context context node.
	 * @param appendable the output to write to.
	 * @return the output passed in returned for convenience.
	 * @throws E if there is an error writing to the output
	 */
	public <A extends Output<E>, E extends Exception> A execute(Object model, //
			ContextNode context, //
			A appendable) throws E;

	/**
	 * Renders the passed in model <strong>with a context</strong> directly to a binary
	 * stream leveraging pre-encoded parts of the template. This <em>may</em> improve
	 * performance when rendering UTF-8 to an OutputStream as some of the encoding is done
	 * in advance. Because the encoding is done statically you cannot pass the charset in.
	 * The chosen charset comes from {@link JStacheConfig#charset()}.
	 * @param <A> output type
	 * @param <E> error type
	 * @param model a model assumed never to be <code>null</code>.
	 * @param context context node.
	 * @param output to write to.
	 * @return the passed in output for convenience
	 * @throws E if an error occurs while writing to output
	 */
	<A extends io.jstach.jstachio.Output.EncodedOutput<E>, E extends Exception> A write( //
			Object model, //
			ContextNode context, //
			A output) throws E;

}
