package io.jstach.jstachio.output;

import java.nio.charset.Charset;

import io.jstach.jstachio.Output.EncodedOutput;

/**
 * An encoded output that forwards all calls to a delegate.
 *
 * @param <E> error throw on any append or write
 */
public abstract class ForwardingEncodedOutput<E extends Exception> extends ForwardingOutput<E>
		implements EncodedOutput<E> {

	@Override
	public void write(byte[] bytes) throws E {
		delegate().write(bytes);
	}

	@Override
	public void write(byte[] bytes, int off, int len) throws E {
		delegate().write(bytes, off, len);
	}

	@Override
	public Charset charset() {
		return delegate().charset();
	}

	@Override
	protected abstract EncodedOutput<E> delegate();

}
