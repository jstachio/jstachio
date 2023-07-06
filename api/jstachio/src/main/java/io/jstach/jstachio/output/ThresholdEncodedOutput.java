package io.jstach.jstachio.output;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

/**
 * This abstract output will limit buffering and then fallback to writing to the
 * downstream output type of <code>T</code> once limit is exceeded. If the limit is not
 * exceed then the data will be replayed and written to when {@linkplain #close() closed}.
 * <p>
 * The output is lazily created once and only once by calling
 * {@link #createConsumer(int)}.
 * <p>
 * <strong>For this implementation to work {@link #close()} must be called and thus a
 * try-with-resource is recommended regardless if the downstream consumer needs to be
 * closed or not! </strong>
 * <p>
 * The total buffered amount of data is not guaranteed to be exactly at the limit even if
 * the total output is greater than the limit.
 *
 * @author agentgt
 * @param <T> the downstream output type
 * @param <E> the exception type that can be thrown while writing to the output type
 * @apiNote This class is not thread safe.
 */
public abstract class ThresholdEncodedOutput<T, E extends Exception> implements CloseableEncodedOutput<E> {

	private final List<byte[]> chunks;

	private final Charset charset;

	private int size = 0;

	protected final int limit;

	private @Nullable T consumer;

	/**
	 * Create with charset and limit.
	 * @param charset the encoding to use.
	 * @param limit the amount of total bytes to limit buffering however the total
	 * buffered amount of data is not guaranteed to be exactly at the limit even if the
	 * total output is greater than the limit.
	 */
	public ThresholdEncodedOutput(Charset charset, int limit) {
		chunks = new ArrayList<>();
		this.charset = charset;
		this.limit = limit;
	}

	/**
	 * Writes to a consumer.
	 * @param consumer the consumer created from {@link #createConsumer(int)}.
	 * @param bytes data to be written
	 */
	protected abstract void write(T consumer, byte[] bytes) throws E;

	/**
	 * Creates the consumer. If size is not <code>-1</code> than the entire output has
	 * been buffered and can be safely used to set <code>Content-Length</code> before
	 * creating the actual consumer (often an OutputStream).
	 * @param size <code>-1</code> indicates that the output is larger than the limit.
	 * @return the created consumer
	 * @throws E an error while creating the consumer
	 */
	protected abstract T createConsumer(int size) throws E;

	/**
	 * Called to close the consumer. Implementations can decide whether or not to really
	 * close the consumer.
	 * @param consumer to be closed or not
	 * @throws E if an error happens while closing.
	 */
	protected abstract void close(T consumer) throws E;

	@Override
	public void write(byte[] bytes) throws E {
		addChunk(bytes);
	}

	private void addChunk(byte[] chunk) throws E {
		int length = chunk.length;
		T c = this.consumer;
		if (c != null) {
			write(c, chunk);
		}
		else if ((length + size) > limit) {
			/*
			 * We have exceeded the threshold
			 */
			c = this.consumer = createConsumer(-1);
			chunks.add(chunk);
			drain(consumer);
		}
		else {
			chunks.add(chunk);
		}
		size += length;
	}

	private void drain(T consumer) throws E {
		for (var chunk : chunks) {
			write(consumer, chunk);
		}
	}

	@Override
	public Charset charset() {
		return charset;
	}

	@Override
	public void close() throws E {
		var c = this.consumer;
		if (c == null) {
			this.consumer = c = createConsumer(size);
			drain(c);
		}
		close(c);
		this.consumer = null;
	}

	/**
	 * This is the current written length.
	 * @return current written length
	 */
	public int size() {
		return size;
	}

	@Override
	public void append(CharSequence s) throws E {
		append(s.toString());
	}

	@Override
	public void append(String s) throws E {
		write(s.getBytes(charset));
	}

}
