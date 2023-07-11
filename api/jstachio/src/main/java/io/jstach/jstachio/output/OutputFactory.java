package io.jstach.jstachio.output;

/**
 * Creates output like objects with a buffer size. Some consumers of this factory may have
 * different semantics on the buffer size parameter but in general <code>-1</code> means
 * the buffer size is unknown.
 *
 * @author agent
 * @param <T> the output type
 * @param <E> the exception type that can be thrown on creation.
 */
@FunctionalInterface
public interface OutputFactory<T, E extends Exception> {

	/**
	 * Create the output type
	 * @param bufferSize buffer size if unknown maybe <code>-1</code>
	 * @return created output
	 * @throws E if any error creating output
	 */
	public T create(int bufferSize) throws E;

}
