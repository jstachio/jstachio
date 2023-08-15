package io.jstach.jstachio.output;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

import io.jstach.jstachio.Output.CloseableEncodedOutput;
import io.jstach.jstachio.Template.EncodedTemplate;

/**
 * An encoded output that will store the output in its preferred memory structure and can
 * then be copied to an {@link OutputStream} or read from a {@link ReadableByteChannel}.
 * The total {@link #size()} of the output can also be retrieved before being copied which
 * is useful for setting "<code>Content-Length</code>" HTTP header or for allocating
 * buffers.
 * <p>
 * <em>The major impetus for this interface is needing the length of the output prior to
 * outputing. If the length is not needed before writing then better performance and
 * memory savings can probably be had by just using
 * {@link EncodedOutput#of(OutputStream, Charset)}.</em>
 * <p>
 * In general instances will be empty and not have correct results till
 * {@link EncodedTemplate#write(Object, EncodedOutput)} is called.
 * <p>
 * Currently there are two approaches to buffered output:
 * <ul>
 * <li>{@link ByteBufferEncodedOutput}: according to benchmarks on most platforms other
 * than Windows it is more CPU friendly but less memory friendly. It is ideal for
 * platforms where you can return {@link ByteBuffer} directly or need to return a
 * <code>byte[]</code>. The default implementation uses a growing array and can be created
 * with {@link ByteBufferEncodedOutput#ofByteArray(Charset, int)} and can be reused
 * provided {@link #close()} is called. Reuse can be useful if using ThreadLocals or some
 * other pooling mechanism.</li>
 * <li>{@link ChunkEncodedOutput}: according to benchmarks is more optimized for memory
 * savings as well as possible reduction of copying. This approach originated from
 * Rocker's purported "near zero copy".</li>
 * </ul>
 * Regardless one should benchmark each approach to determine which one best fits for
 * their platform. In many cases a custom {@link EncodedOutput} that uses the platform's
 * underlying buffer pool will probably yield the best results.
 *
 * @author agentgt
 * @see ByteBufferEncodedOutput
 * @see ChunkEncodedOutput
 * @apiNote Methods starting with "<code>as</code>" are a view where as methods starting
 * with "<code>to</code>" (or ending in "To" for the case of transferTo) are a copy. If
 * the output is to be reused "<code>as</code>" methods should be copied or fully used
 * before the output is reused.
 */
@SuppressWarnings("rawtypes") // this is an eclipse bug
public sealed interface BufferedEncodedOutput
		extends CloseableEncodedOutput<RuntimeException>permits ChunkEncodedOutput, ByteBufferEncodedOutput {

	/**
	 * Total size in number of bytes of the output.
	 * @return size
	 * @see #bufferSizeHint()
	 */
	public int size();

	/**
	 * Transfers the entire buffered output by writing to an OutputStream.
	 * @param stream not null and will not be closed or flushed.
	 * @throws IOException if the stream throws an IOException.
	 * @see #asReadableByteChannel()
	 * @apiNote For nonblocking {@link #asReadableByteChannel()} is generally accepted as
	 * the better aproach as it is a pull model.
	 */
	default void transferTo(OutputStream stream) throws IOException {
		accept(stream::write);
	}

	/**
	 * Decorates this buffer so that buffering is limited to certain amount and will
	 * eventually send all output to the OutputStream created by the factory. The factory
	 * will be passed <code>-1</code> if the limit is exceeded and
	 * {@linkplain OutputFactory#create(int) create} will only be called once and only
	 * once provided that the returned object is closed.
	 * <p>
	 * This method should be called before passed to JStachio and the result is the output
	 * that should be passed.
	 * @param limit the maximum amount of bytes to buffer.
	 * @param factory create the output stream on demand and will always be used before
	 * close is called.
	 * @return output that will need to be closed eventually.
	 * @see LimitEncodedOutput
	 */
	default LimitEncodedOutput<OutputStream, IOException> limit(int limit,
			OutputFactory<OutputStream, IOException> factory) {
		return new AbstractLimitEncodedOutput(this, limit) {
			@Override
			protected OutputStream createConsumer(int size) throws IOException {
				return factory.create(size);
			}
		};
	}

	/**
	 * Transfers the entire buffered output to a consumer
	 * @param <E> the exception type
	 * @param consumer not null.
	 * @throws E if the consumer throws an exception
	 * @see #asReadableByteChannel()
	 * @apiNote For nonblocking {@link #asReadableByteChannel()} is generally accepted as
	 * the better aproach as it is a pull model.
	 */
	public <E extends Exception> void accept(OutputConsumer<E> consumer) throws E;

	/**
	 * The recommend buffer size to use for extracting with
	 * {@link #asReadableByteChannel()} or {@link #transferTo(OutputStream)}.
	 * @return buffer size to use which by default is {@link #size()}.
	 */
	default int bufferSizeHint() {
		return size();
	}

	/**
	 * Represents the encoded output as readable channel. <strong>The channel should be
	 * closed when finished to signal reuse or destruction of the buffered
	 * output!</strong>. To possibly help determine the buffer to use for
	 * {@link ReadableByteChannel#read(java.nio.ByteBuffer)} one can use
	 * {@link #bufferSizeHint()} or {@link #size()}.
	 * @return channel open and ready to read from at the start of the output.
	 * @see #bufferSizeHint()
	 */
	public ReadableByteChannel asReadableByteChannel();

	/**
	 * <strong>Copies</strong> the output to a byte array.
	 * @return a copied byte array of the output
	 */
	default byte[] toByteArray() {
		int size = size();
		byte[] result = new byte[size];
		OutputConsumer<RuntimeException> consumer = new OutputConsumer<>() {
			int index = 0;

			@Override
			public void accept(byte[] data, int offset, int length) throws RuntimeException {
				System.arraycopy(data, offset, result, index, length);
				index += length;
			}
		};
		accept(consumer);
		return result;
	}

	@Override
	default void append(String s) {
		write(s.getBytes(charset()));
	}

	@Override
	default void append(CharSequence s) {
		append(s.toString());
	}

	/**
	 * Signals that the buffer should be reset for reuse or destroyed.
	 * @apiNote This does not throw an IOException on purpose since everything is in
	 * memory.
	 */
	@Override
	public void close();

	/**
	 * If this instance can be reused after {@link #close()} is called.
	 * @return true if reuse is allowed by default false is returned.
	 */
	default boolean isReusable() {
		return false;
	}

	/**
	 * Create a buffered encoded output backed by a sequence of chunks.
	 * @param charset the expected encoding
	 * @return buffered output
	 */
	public static BufferedEncodedOutput ofChunked(Charset charset) {
		return ChunkEncodedOutput.ofByteArrays(charset);
	}

}
