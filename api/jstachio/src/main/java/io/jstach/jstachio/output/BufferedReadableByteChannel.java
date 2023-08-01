package io.jstach.jstachio.output;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.Nullable;

/**
 * A readable channel representing an already fully loaded output in memory.
 *
 * @author agentgt
 */
interface BufferedReadableByteChannel extends ReadableByteChannel {

	@Override
	public int read(ByteBuffer dst);

	@Override
	public void close();

	/**
	 * Total size of the encoded output.
	 * @return total size of the encoded output
	 */
	public int size();

	static BufferedReadableByteChannel of(BufferedEncodedOutput output, ByteBuffer buffer) {
		return new ByteBufferChannel(buffer, output);
	}

	static BufferedReadableByteChannel of(BufferedEncodedOutput output, final List<byte[]> arrays) {
		int length = output.size();
		return new BufferedReadableByteChannel() {

			private boolean closed = false;

			private int offset = 0;

			private int chunkIndex = 0;

			private int chunkOffset = 0;

			@Override
			public int read(ByteBuffer dst) {

				// end of stream?
				if (arrays.isEmpty() || offset >= length) {
					return -1;
				}

				int readBytes = 0;

				// keep trying to fill up buffer while it has capacity and we
				// still have data to fill it up with
				while (dst.hasRemaining() && (offset < length)) {

					byte[] chunk = arrays.get(chunkIndex);
					int chunkLength = chunk.length - chunkOffset;

					// number of bytes capable of being read
					int capacity = dst.remaining();
					if (capacity < chunkLength) {
						chunkLength = capacity;
					}

					dst.put(chunk, chunkOffset, chunkLength);

					// update everything
					offset += chunkLength;
					chunkOffset += chunkLength;

					if (chunkOffset >= chunk.length) {
						// next chunk next time
						chunkIndex++;
						chunkOffset = 0;
					}

					readBytes += chunkLength;
				}

				return readBytes;
			}

			@Override
			public boolean isOpen() {
				return !closed;
			}

			@Override
			public int size() {
				return length;
			}

			@Override
			public void close() {
				closed = true;
				output.close();
			}
		};
	}

	static BufferedReadableByteChannel of(BufferedEncodedOutput output, final Iterator<byte[]> arrays) {
		Supplier<byte @Nullable []> sup = () -> {
			if (!arrays.hasNext()) {
				return null;
			}
			return arrays.next();
		};
		return of(output, sup);
	}

	static BufferedReadableByteChannel of(BufferedEncodedOutput output, final Supplier<byte @Nullable []> arrays) {
		int length = output.size();

		return new BufferedReadableByteChannel() {

			private boolean closed = false;

			private int chunkOffset = 0;

			byte @Nullable [] current = arrays.get();

			@Override
			public int read(ByteBuffer dst) {
				if (current == null) {
					return -1;
				}

				int readBytes = 0;

				byte[] chunk;
				while ((chunk = current) != null && dst.hasRemaining()) {

					int chunkLength = chunk.length - chunkOffset;

					// number of bytes capable of being read
					int capacity = dst.remaining();
					if (capacity < chunkLength) {
						chunkLength = capacity;
					}

					dst.put(chunk, chunkOffset, chunkLength);

					// update everything
					chunkOffset += chunkLength;

					if (chunkOffset >= chunk.length) {
						current = arrays.get();
						chunkOffset = 0;
					}

					readBytes += chunkLength;
				}

				return readBytes;
			}

			@Override
			public boolean isOpen() {
				return !closed;
			}

			@Override
			public int size() {
				return length;
			}

			@Override
			public void close() {
				closed = true;
				output.close();
			}
		};
	}

}

class ByteBufferChannel implements BufferedReadableByteChannel {

	private final ByteBuffer buffer;

	private final BufferedEncodedOutput output;

	private boolean closed = false;

	public ByteBufferChannel(ByteBuffer buffer, BufferedEncodedOutput output) {
		this.buffer = buffer;
		this.output = output;
	}

	@Override
	public int read(ByteBuffer dst) {
		int remaining = buffer.remaining();
		int bytesToRead = Math.min(remaining, dst.remaining());
		if (bytesToRead > 0) {
			int oldLimit = buffer.limit();
			buffer.limit(buffer.position() + bytesToRead);
			dst.put(buffer);
			buffer.limit(oldLimit);
			return bytesToRead;
		}
		return -1;
	}

	@Override
	public boolean isOpen() {
		return closed;
	}

	@Override
	public void close() {
		this.closed = true;
		this.output.close();
	}

	@Override
	public int size() {
		return output.size();
	}

}
