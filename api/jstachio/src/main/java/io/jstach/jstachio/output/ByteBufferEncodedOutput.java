package io.jstach.jstachio.output;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

/**
 * An encoded output optimized for producing a single {@link ByteBuffer}.
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

	@Override
	default ReadableByteChannel asReadableByteChannel() {
		return BufferedReadableByteChannel.of(this, asByteBuffer());
	}

}
