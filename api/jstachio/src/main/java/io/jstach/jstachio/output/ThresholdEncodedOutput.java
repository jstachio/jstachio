package io.jstach.jstachio.output;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

/**
 * This abstract output will {@linkplain #limit limit} buffering by byte count and then
 * fallback to pushing to the downstream output type of <code>T</code> once limit is
 * exceeded. If the limit is not exceeded then the buffered data will be replayed and
 * pushed when {@linkplain #close() closed}. <em>Consequently this output strategy is a
 * better fit for integration of blocking APIs such as Servlet based frameworks where the
 * data is pushed instead of pulled. If pulling is more desired (non blocking code
 * generally prefers a pull approach) than {@link BufferedEncodedOutput} is a better fit
 * but requires the entire output be buffered.</em>
 * <p>
 * The output <code>T</code> is lazily created once and only once by calling
 * {@link #createConsumer(int)} and the total size buffered will be passed if under limit.
 * If the limit is exceeded than the size passed will be <code>-1</code>.
 * <p>
 * <strong>For this implementation to work {@link #close()} must be called and thus a
 * try-with-resource is recommended regardless if the downstream consumer needs to be
 * closed or not! </strong>
 * <p>
 * The total buffered amount of data is not guaranteed to be exactly at the limit even if
 * the total output is greater than the limit.
 * <p>
 * The advantages to letting this implementation do the buffering instead of the
 * downstream framework is saving memory in that the pre-encoded parts of the template are
 * just pointers and are not copied multiple times. Since this output instance will be
 * doing the buffering it is important to minimize downstream buffering by letting the
 * framework know that it does not need to buffer.
 *
 * <h2>Example for Servlet output:</h2> <pre><code class="language-java">
 * class ServletThresholdEncodedOutput extends ThresholdEncodedOutput.OutputStreamThresholdEncodedOutput {
 *
 *     private final HttpServletResponse response;
 *
 *     public ServletThresholdEncodedOutput(Charset charset, HttpServletResponse response) {
 *         super(charset, calculateLimit(response));
 *         this.response = response;
 *     }
 *
 *     private static int calculateLimit(HttpServletResponse response) {
 *         int limit = response.getBufferSize();
 *         if (limit &lt;= 0) {
 *             return 1024 * 32;
 *         }
 *         return limit;
 *     }
 *
 *     &#64;Override
 *     protected OutputStream createConsumer(int size) throws IOException {
 *         if (size > -1) {
 *             response.setContentLength(size);
 *              // It is already all in memory so we do not need a buffer.
 *             response.setBufferSize(0);
 *         }
 *         return response.getOutputStream();
 *     }
 *
 * }
 * </code> </pre>
 *
 * @author agentgt
 * @param <T> the downstream output type
 * @param <E> the exception type that can be thrown while writing to the output type
 * @apiNote This class is not thread safe.
 * @see OutputStreamThresholdEncodedOutput
 */
public abstract non-sealed class ThresholdEncodedOutput<T, E extends Exception> implements LimitEncodedOutput<T, E> {

	private final List<byte[]> chunks;

	private final Charset charset;

	private int size = 0;

	/**
	 * The maximum number of bytes to buffer.
	 */
	protected final int limit;

	private @Nullable T consumer;

	/**
	 * Create with charset and limit.
	 * @param charset the encoding to use.
	 * @param limit the amount of total bytes to limit buffering however the total
	 * buffered amount of data is not guaranteed to be exactly at the limit even if the
	 * total output is greater than the limit.
	 */
	protected ThresholdEncodedOutput(Charset charset, int limit) {
		chunks = new ArrayList<>();
		this.charset = charset;
		this.limit = limit;
	}

	/**
	 * Writes to a consumer.
	 * @param consumer the consumer created from {@link #createConsumer(int)}.
	 * @param bytes data to be written
	 * @throws E if an error happens while using the consumer.
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
	public @Nullable T consumer() {
		return this.consumer;
	}

	@Override
	public Charset charset() {
		return charset;
	}

	/**
	 * If the limit is not exceeded then the buffered data will be replayed and pushed
	 * when closed. Regardless {@link #close(Object)} will be called on the output like
	 * object.
	 * @throws E if an error happens while creating or closing the downstream output
	 */
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
	public int limit() {
		return limit;
	}

	@Override
	public void append(CharSequence s) throws E {
		append(s.toString());
	}

	@Override
	public void append(String s) throws E {
		write(s.getBytes(charset));
	}

	/**
	 * An OutputStream backed buffer limited encoded output. This partial implementation
	 * will cascade {@link #close()} to the OutputStream similar to OutputStream
	 * decorators in the JDK.
	 *
	 * @author agentgt
	 */
	public abstract static class OutputStreamThresholdEncodedOutput
			extends ThresholdEncodedOutput<OutputStream, IOException> {

		/**
		 * Create with charset and limit.
		 * @param charset the encoding to use.
		 * @param limit the amount of total bytes to limit buffering however the total
		 * buffered amount of data is not guaranteed to be exactly at the limit even if
		 * the total output is greater than the limit.
		 */
		protected OutputStreamThresholdEncodedOutput(Charset charset, int limit) {
			super(charset, limit);
		}

		@Override
		protected void write(OutputStream consumer, byte[] bytes) throws IOException {
			consumer.write(bytes);
		}

		@Override
		protected void close(OutputStream consumer) throws IOException {
			consumer.close();
		}

	}

}
