package io.jstach.jstachio.output;

import java.nio.ByteBuffer;

import io.jstach.jstachio.Output.EncodedOutput;

public interface BufferedEncodedOutput extends EncodedOutput<RuntimeException>, AutoCloseable {
	public int size();
	public ByteBuffer toBuffer();
	default byte[] getInternalBuffer() {
		return toBuffer().array();
	}
	default void close () {
	}
}
