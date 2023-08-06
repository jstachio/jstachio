package io.jstach.jstachio.output;

import io.jstach.jstachio.Output;

/**
 * An encoded output that forwards all calls to a delegate.
 *
 * @param <E> error throw on any append or write
 */
public abstract class ForwardingOutput<E extends Exception> implements Output<E> {

	/**
	 * The output to forward to.
	 * @return output to forward to.
	 */
	protected abstract Output<E> delegate();

	@Override
	public void append(CharSequence s) throws E {
		delegate().append(s);
	}

	@Override
	public void append(short s) throws E {
		delegate().append(s);
	}

	@Override
	public void append(int i) throws E {
		delegate().append(i);
	}

	@Override
	public void append(long l) throws E {
		delegate().append(l);
	}

	@Override
	public void append(double d) throws E {
		delegate().append(d);
	}

	@Override
	public void append(boolean b) throws E {
		delegate().append(b);
	}

	@Override
	public Appendable toAppendable() {
		return delegate().toAppendable();
	}

	@Override
	public void append(char c) throws E {
		delegate().append(c);
	}

	@Override
	public void append(String s) throws E {
		delegate().append(s);
	}

	@Override
	public void append(CharSequence csq, int start, int end) throws E {
		delegate().append(csq, start, end);
	}

}
