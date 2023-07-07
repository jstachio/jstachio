package io.jstach.jstachio.output;

import java.io.OutputStream;

/**
 * An OutputStream like callback.
 *
 * @param <E> exception that could be thrown while accepting byte data.
 */
@FunctionalInterface
public interface OutputConsumer<E extends Exception> extends AutoCloseable {

	/**
	 * Convenience method that will call the real accept.
	 * @param data array to be fully copied
	 * @throws E if consumer has an error
	 */
	default void accept(byte[] data) throws E {
		accept(data, 0, data.length);
	}

	/**
	 * Analagous to {@link OutputStream#write(byte[], int, int)}.
	 * @param data data
	 * @param offset offset
	 * @param length length
	 * @throws E if the consumer as an error
	 */
	public void accept(byte[] data, int offset, int length) throws E;

	@Override
	default void close() throws E {
	}

}