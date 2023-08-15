package io.jstach.jstachio.output;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 *
 * A custom OutputStream that is designed for generating bytes from pre-encoded output as
 * well as reused <em>carefully</em> either by threadlocals or some other pooling
 * mechanism.
 * <p>
 * If the buffer is to be reused {@link #close()} should be called first before it is used
 * or after every time it is used and {@link #toBuffer()} should be called to get a
 * correct view of the internal buffer.
 * <p>
 * This is basically the same as Joobys Rockers byte buffer but as an OutputStream because
 * JStachio wants that interface.
 *
 * Consequently this code was heavily inspired from
 * <a href="https://github.com/jooby-project/jooby">Jooby's</a> custom Rocker Output.
 * <p>
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt Copyright 2014 Edgar Espina
 *
 * @author agentgt
 * @author jknack
 */
public class ByteBufferedOutputStream extends OutputStream implements ByteBufferEncodedOutput {

	/** Default buffer size: <code>4k</code>. */
	public static final int BUFFER_SIZE = 4096;

	/**
	 * The maximum size of array to allocate. Some VMs reserve some header words in an
	 * array. Attempts to allocate larger arrays may result in OutOfMemoryError: Requested
	 * array size exceeds VM limit
	 */
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	/** The buffer where data is stored. */
	protected byte[] buf;

	/** The number of valid bytes in the buffer. */
	protected int count;

	/**
	 * The expected charset of the output.
	 */
	protected final Charset charset;

	/**
	 * Creates buffered stream of given size.
	 * @param bufferSize initial size.
	 */
	public ByteBufferedOutputStream(int bufferSize) {
		this(bufferSize, StandardCharsets.UTF_8);
	}

	/**
	 * Creates buffered Output of given size and given charset if used as a Jstachio
	 * Output.
	 * @param bufferSize initial size.
	 * @param charset the charset of the output
	 */
	public ByteBufferedOutputStream(int bufferSize, Charset charset) {
		this.buf = new byte[bufferSize];
		this.charset = charset;
	}

	/**
	 * Creates buffered stream of buffer initial size: {@value #BUFFER_SIZE}.
	 */
	public ByteBufferedOutputStream() {
		this(BUFFER_SIZE);
	}

	void reset() {
		count = 0;
	}

	@Override
	public void close() {
		this.reset();
	}

	@Override
	public Charset charset() {
		return this.charset;
	}

	@Override
	public void write(byte[] bytes) {
		int len = bytes.length;
		ensureCapacity(count + len);
		System.arraycopy(bytes, 0, buf, count, len);
		count += len;
	}

	@Override
	public void write(byte[] bytes, int off, int len) {
		ensureCapacity(count + len);
		System.arraycopy(bytes, off, buf, count, len);
		count += len;
	}

	@Override
	public void append(String s) {
		write(s.getBytes(this.charset));
	}

	/**
	 * How many bytes have been written so far.
	 * @return 0 if empty, otherwise how many bytes so far
	 */
	@Override
	public int size() {
		return count;
	}

	/**
	 * Copy internal byte array into a new array.
	 * @return Byte array.
	 */
	@Override
	public byte[] toByteArray() {
		byte[] array = new byte[count];
		System.arraycopy(buf, 0, array, 0, count);
		return array;
	}

	/**
	 * Get a view of the internal byte buffer. <strong>Care must be taken if this instance
	 * is to be reused in multithreaded environment!</strong>
	 * @return Byte buffer.
	 */
	public ByteBuffer toBuffer() {
		return ByteBuffer.wrap(buf, 0, count);
	}

	@Override
	public ByteBuffer asByteBuffer() {
		return toBuffer();
	}

	private void ensureCapacity(int minCapacity) {
		// overflow-conscious code
		if (minCapacity - buf.length > 0) {
			grow(minCapacity);
		}
	}

	/**
	 * Increases the capacity to ensure that it can hold at least the number of elements
	 * specified by the minimum capacity argument.
	 * @param minCapacity the desired minimum capacity
	 */
	private void grow(int minCapacity) {
		// overflow-conscious code
		int oldCapacity = buf.length;
		int newCapacity = oldCapacity << 1;
		if (newCapacity - minCapacity < 0) {
			newCapacity = minCapacity;
		}
		if (newCapacity - MAX_ARRAY_SIZE > 0) {
			newCapacity = hugeCapacity(minCapacity);
		}
		buf = Arrays.copyOf(buf, newCapacity);
	}

	private static int hugeCapacity(int minCapacity) {
		if (minCapacity < 0) {
			throw new OutOfMemoryError();
		}
		return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
	}

	@Override
	public void write(int b) {
		throw new UnsupportedOperationException("expecting only write(byte[])");
	}

	@Override
	public void transferTo(OutputStream stream) throws IOException {
		stream.write(buf, 0, count);
	}

	@Override
	public <E extends Exception> void accept(OutputConsumer<E> consumer) throws E {
		consumer.accept(buf, 0, count);
	}

	@Override
	public boolean isReusable() {
		return true;
	}

}
