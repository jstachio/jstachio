package io.jstach.jstachio.output;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

/**
 * An encoded output optimized for producing a single {@link ByteBuffer}. The
 * {@link #bufferSizeHint()} is usually the size of the entire output and usually
 * implementations of this type are {@linkplain #isReusable() reusable} with care.
 *
 * @author agentgt
 */
public non-sealed interface ByteBufferEncodedOutput extends BufferedEncodedOutput {

	/**
	 * Gets a byte buffer view of the data.
	 * @return byte buffer
	 */
	public ByteBuffer asByteBuffer();

	/**
	 * Create a buffered encoded output backed by an array that will grow as needed
	 * analagous to StringBuilder and/or {@link ByteArrayOutputStream}. <strong>This
	 * output is more optimized for getting byte array or a ByteBuffer as well a reuse.
	 * </strong>
	 * @param charset the expected encoding
	 * @param initialSize the initial size of the backing array.
	 * @return buffered output
	 */
	public static ByteBufferEncodedOutput ofByteArray(Charset charset, int initialSize) {
		return new ByteBufferedOutputStream(initialSize, charset);
	}

	/**
	 * Calls {@link #ofByteArray(Charset, int)} with initial size of
	 * {@value ByteBufferedOutputStream#BUFFER_SIZE}.
	 * @param charset the expected encoding
	 * @return buffered output
	 */
	public static ByteBufferEncodedOutput ofByteArray(Charset charset) {
		return new ByteBufferedOutputStream(ByteBufferedOutputStream.BUFFER_SIZE, charset);
	}

	@Override
	default ReadableByteChannel asReadableByteChannel() {
		return BufferedReadableByteChannel.of(this, asByteBuffer());
	}

}
