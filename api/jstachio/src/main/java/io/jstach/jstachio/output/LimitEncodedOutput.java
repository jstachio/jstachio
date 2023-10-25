package io.jstach.jstachio.output;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstachio.Output.CloseableEncodedOutput;

/**
 * This output will {@linkplain #limit() limit} buffering by byte count and then fallback
 * to pushing to the downstream output type of <code>T</code> once limit is exceeded. If
 * the limit is not exceeded then the buffered data will be replayed and pushed when
 * {@linkplain #close() closed}. <em>Consequently this output strategy is a better fit for
 * integration of blocking APIs such as Servlet based frameworks where the data is pushed
 * instead of pulled. If pulling is more desired (non blocking code generally prefers a
 * pull approach) than {@link BufferedEncodedOutput} is a better fit but requires the
 * entire output be buffered.</em>
 * <p>
 * The output <code>T</code> is generally lazily created once and only once by calling and
 * the total size buffered will be passed if under limit. If the limit is exceeded than
 * the size passed will be <code>-1</code>.
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
 * @see BufferedEncodedOutput#limit(int, OutputFactory)
 * @see ThresholdEncodedOutput
 */
public sealed interface LimitEncodedOutput<T, E extends Exception>
		extends CloseableEncodedOutput<E>permits AbstractLimitEncodedOutput, ThresholdEncodedOutput {

	/**
	 * Buffer limit
	 * @return limit buffer to this amount of bytes
	 */
	public int limit();

	/**
	 * Current amount of bytes written.
	 * @return number of bytes written.
	 */
	public int size();

	/**
	 * The created consumer. Maybe <code>null</code> but on successful close should not
	 * be. <strong>This is not to create the consumer but to fetch it after processing has
	 * finished</strong> since the consumer is created on demand.
	 * @return created consumer
	 */
	public @Nullable T consumer();

}

/**
 * This is purposely not public at the moment.
 *
 * @author agentgt
 */
abstract non-sealed class AbstractLimitEncodedOutput implements LimitEncodedOutput<OutputStream, IOException> {

	private final BufferedEncodedOutput buffer;

	protected final int limit;

	protected @Nullable OutputStream consumer;

	protected int size = 0;

	protected final Charset charset;

	protected AbstractLimitEncodedOutput(BufferedEncodedOutput buffer, int limit) {
		super();
		this.buffer = buffer;
		this.limit = limit;
		this.charset = buffer.charset();
	}

	@Override
	public void write(byte[] chunk) throws IOException {
		int length = chunk.length;
		OutputStream c = this.consumer;
		if (c != null) {
			c.write(chunk);
		}
		else if ((length + size) > limit) {
			/*
			 * We have exceeded the threshold
			 */
			c = this.consumer = createConsumer(-1);
			buffer.transferTo(c);
			c.write(chunk);
		}
		else {
			buffer.write(chunk);
		}
		size += length;

	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int limit() {
		return limit;
	}

	@Override
	public @Nullable OutputStream consumer() {
		return consumer;
	}

	/**
	 * Creates the consumer. If size is not <code>-1</code> than the entire output has
	 * been buffered and can be safely used to set <code>Content-Length</code> before
	 * creating the actual consumer (often an OutputStream).
	 * @param size <code>-1</code> indicates that the output is larger than the limit.
	 * @return the created consumer
	 * @throws E an error while creating the consumer
	 */
	protected abstract OutputStream createConsumer(int size) throws IOException;

	/**
	 * Writes to a consumer.
	 * @param consumer the consumer created from {@link #createConsumer(int)}.
	 * @param bytes data to be written
	 * @throws E if an error happens while using the consumer.
	 */
	protected void write(OutputStream consumer, byte[] bytes) throws IOException {
		consumer.write(bytes);
	}

	/**
	 * Called to close the consumer. Implementations can decide whether or not to really
	 * close the consumer.
	 * @param consumer to be closed or not
	 * @throws IOException if an error happens while closing.
	 */
	protected void close(OutputStream consumer) throws IOException {
		consumer.close();
	}

	@Override
	public Charset charset() {
		return charset;
	}

	@Override
	public void append(CharSequence s) throws IOException {
		append(s.toString());
	}

	@Override
	public void append(String s) throws IOException {
		write(s.getBytes(charset));
	}

	/**
	 * If the limit is not exceeded then the buffered data will be replayed and pushed
	 * when closed. Regardless {@link #close(OutputStream)} will be called on the output
	 * like object.
	 * @throws IOException if an error happens while creating or closing the downstream
	 * output
	 */
	@Override
	public void close() throws IOException {
		var c = this.consumer;
		if (c == null) {
			this.consumer = c = createConsumer(size);
			this.buffer.transferTo(c);
		}
		close(c);
		this.consumer = null;
	}

}
