package io.jstach.jstachio.output;

import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Maintains the encoded output in an iterable of chunks of type <code>T</code> that is
 * optimized for {@link #asReadableByteChannel()}.
 *
 * @author agentgt
 * @param <T> the chunk type
 */
public non-sealed interface ChunkEncodedOutput<T> extends BufferedEncodedOutput {

	/**
	 * Gets the internal sequence of chunks.
	 * @return sequence of chunks
	 */
	public Iterable<T> getChunks();

	/**
	 * A chunk encoded output backed by a list of byte arrays
	 * @param charset the expected charset
	 * @return encoded output ready to be written to.
	 */
	static ChunkEncodedOutput<byte[]> ofByteArrays(Charset charset) {
		return new ByteArrayChunkEncodedOutput(charset);
	}

	/**
	 * For chunk output the buffer hint is usually the size of the largest chunk.
	 * {@inheritDoc}
	 */
	@Override
	public int bufferSizeHint();

}

class ByteArrayChunkEncodedOutput implements ChunkEncodedOutput<byte[]> {

	private final List<byte[]> chunks;

	private final Charset charset;

	private int size = 0;

	private int bufferSizeHint = 0;

	public ByteArrayChunkEncodedOutput(Charset charset) {
		chunks = new ArrayList<>();
		this.charset = charset;
	}

	@Override
	public void write(byte[] bytes) {
		addChunk(bytes);
	}

	private void addChunk(byte[] chunk) {
		chunks.add(chunk);
		int length = chunk.length;
		size += length;
		if (bufferSizeHint < length) {
			bufferSizeHint = length;
		}
	}

	public byte[] toByteArray() {
		byte[] result = new byte[size];

		int index = 0;
		for (byte[] chunk : chunks) {
			System.arraycopy(chunk, 0, result, index, chunk.length);
			index += chunk.length;
		}

		return result;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int bufferSizeHint() {
		return bufferSizeHint;
	}

	@Override
	public Charset charset() {
		return charset;
	}

	@Override
	public <E extends Exception> void accept(OutputConsumer<E> consumer) throws E {
		for (byte[] chunk : chunks) {
			consumer.accept(chunk);
		}
	}

	@Override
	public ReadableByteChannel asReadableByteChannel() {
		return BufferedReadableByteChannel.of(this, chunks);
	}

	@Override
	public void close() {
		// We do nothing here as doing something will not really help anything
		// as this output is not designed for reuse.
	}

	@Override
	public Iterable<byte[]> getChunks() {
		return chunks;
	}

}
