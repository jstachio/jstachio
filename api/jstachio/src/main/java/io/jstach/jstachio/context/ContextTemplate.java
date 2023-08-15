package io.jstach.jstachio.context;

import io.jstach.jstache.JStacheConfig;
import io.jstach.jstachio.Output;
import io.jstach.jstachio.Output.EncodedOutput;
import io.jstach.jstachio.Template;

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

	/**
	 * Creates a context template from a regular template if is not already a context
	 * template.
	 * @param <T> model type
	 * @param template the template to be wrapped
	 * @return context template
	 */
	@SuppressWarnings({ "null", "unchecked" })
	public static <T> ContextTemplate<T> of(Template<T> template) {
		if (template instanceof ContextTemplate ct) {
			return ct;
		}
		return new DecoratedContextTemplate<>(template);
	}

}

record DecoratedContextTemplate<T> (Template<T> template) implements ContextTemplate<T> {

	@Override
	public <A extends Output<E>, E extends Exception> A execute(T model, ContextNode context, A appendable) throws E {
		var out = ContextAwareOutput.of(appendable, context);
		template.execute(model, out);
		return appendable;
	}

	@Override
	public <A extends EncodedOutput<E>, E extends Exception> A write(T model, ContextNode context, A output) throws E {
		var out = ContextAwareOutput.of(output, context);
		template.write(model, out);
		return output;
	}

}
