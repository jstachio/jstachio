package io.jstach.jstachio.context;

import io.jstach.jstachio.Output;
import io.jstach.jstachio.Output.EncodedOutput;
import io.jstach.jstachio.output.ForwardingEncodedOutput;
import io.jstach.jstachio.output.ForwardingOutput;

/**
 * Decorate outputs with a context.
 *
 * @author agentgt
 * @see ContextJStachio
 */
sealed interface ContextAwareOutput<O> extends ContextSupplier {

	/**
	 * The original output
	 * @return the original output
	 */
	O getOutput();

	/**
	 * Wrap an output with a context.
	 * @param <E> exception type
	 * @param <O> output type
	 * @param output output to wrap
	 * @param context context to use
	 * @return decorated output
	 */
	static <E extends Exception, O extends Output<E>> ContextOutput<E, O> of(O output, ContextNode context) {
		return new ContextOutput<>(output, context);
	}

	/**
	 * Wrap an encoded output with a context.
	 * @param <E> exception type
	 * @param <O> output type
	 * @param output output to wrap
	 * @param context context to use
	 * @return decorated output
	 */
	static <E extends Exception, O extends EncodedOutput<E>> ContextEncodedOutput<E, O> of(O output,
			ContextNode context) {
		return new ContextEncodedOutput<>(output, context);
	}

	/**
	 * A decorated output containing a context.
	 *
	 * @param <E> exception type
	 * @param <O> output type
	 */
	final class ContextOutput<E extends Exception, O extends Output<E>> extends ForwardingOutput<E>
			implements ContextAwareOutput<O> {

		private final O output;

		private final ContextNode context;

		private ContextOutput(O output, ContextNode context) {
			super();
			this.output = output;
			this.context = context;
		}

		@Override
		public ContextNode context() {
			return this.context;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected O delegate() {
			return this.output;
		}

		public O getOutput() {
			return output;
		}

	}

	/**
	 * A decorated output containing a context.
	 *
	 * @param <E> exception type
	 * @param <O> output type
	 */
	public final class ContextEncodedOutput<E extends Exception, O extends EncodedOutput<E>>
			extends ForwardingEncodedOutput<E> implements ContextAwareOutput<O> {

		private final O output;

		private final ContextNode context;

		private ContextEncodedOutput(O output, ContextNode context) {
			super();
			this.output = output;
			this.context = context;
		}

		@Override
		public ContextNode context() {
			return this.context;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected O delegate() {
			return this.output;
		}

		public O getOutput() {
			return output;
		}

	}

}
