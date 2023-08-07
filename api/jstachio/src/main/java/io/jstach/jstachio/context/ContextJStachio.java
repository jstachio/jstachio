package io.jstach.jstachio.context;

import java.io.IOException;

import io.jstach.jstache.JStacheConfig;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Output;
import io.jstach.jstachio.Output.EncodedOutput;
import io.jstach.jstachio.Template;

/**
 * A special JStachio that can render models with a loose typed context object bound to
 * {@value ContextNode#CONTEXT_BINDING_NAME}.
 *
 * @see JStachio
 * @see ContextNode
 * @since 1.3.0
 * @author agentgt
 */
public interface ContextJStachio extends JStachio {

	/**
	 * Renders the passed in model <strong>with a context</strong>.
	 * @param <A> output type
	 * @param <E> error type
	 * @param model a model assumed never to be <code>null</code>.
	 * @param context context node bound to {@value ContextNode#CONTEXT_BINDING_NAME}.
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
	 * @param context context node bound to {@value ContextNode#CONTEXT_BINDING_NAME}.
	 * @param output to write to.
	 * @return the passed in output for convenience
	 * @throws E if an error occurs while writing to output
	 */
	<A extends io.jstach.jstachio.Output.EncodedOutput<E>, E extends Exception> A write( //
			Object model, //
			ContextNode context, //
			A output) throws E;

	/**
	 * Decorates a JStachio instance as a {@link ContextJStachio} if it is not already
	 * one.
	 * @param jstachio the instance to be wrapped.
	 * @return the passed in jstachio if it is already a {@link ContextJStachio} otherwise
	 * wraps the passed in instance.
	 */
	public static ContextJStachio of(JStachio jstachio) {
		if (jstachio instanceof ContextJStachio cj) {
			return cj;
		}
		return new ForwardingJStachio(jstachio);
	}

}

record ForwardingJStachio(JStachio delegate) implements ContextJStachio {

	@Override
	public <A extends Output<E>, E extends Exception> A execute(Object model, A appendable) throws E {
		return delegate.execute(model, appendable);
	}

	@Override
	public <A extends EncodedOutput<E>, E extends Exception> A write(Object model, A output) throws E {
		return delegate.write(model, output);
	}

	@Override
	public Template<Object> findTemplate(Object model) throws Exception {
		return delegate.findTemplate(model);
	}

	@Override
	public boolean supportsType(Class<?> modelType) {
		return delegate.supportsType(modelType);
	}

	@Override
	public void execute(Object model, Appendable appendable) throws IOException {
		delegate.execute(model, appendable);
	}

	@Override
	public StringBuilder execute(Object model, StringBuilder sb) {
		return delegate.execute(model, sb);
	}

	@Override
	public String execute(Object model) {
		return delegate.execute(model);
	}

	@Override
	public <A extends Output<E>, E extends Exception> A execute(Object model, ContextNode context, A appendable)
			throws E {
		var out = ContextAwareOutput.of(appendable, context);
		return delegate.execute(model, out).getOutput();
	}

	@Override
	public <A extends EncodedOutput<E>, E extends Exception> A write(Object model, ContextNode context, A output)
			throws E {
		var out = ContextAwareOutput.of(output, context);
		return delegate.write(model, out).getOutput();
	}

}
