package io.jstach.jstachio.output;

import io.jstach.jstachio.Output.EncodedOutput;

/**
 * An output that should be closed
 *
 * @author agent
 * @param <E> error on close
 */
public interface CloseableEncodedOutput<E extends Exception> extends EncodedOutput<E>, AutoCloseable {

	@Override
	public void close() throws E;

}
