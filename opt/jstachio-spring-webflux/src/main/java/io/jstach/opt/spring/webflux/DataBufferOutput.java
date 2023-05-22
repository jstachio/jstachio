package io.jstach.opt.spring.webflux;

import java.nio.charset.Charset;

import org.springframework.core.io.buffer.DataBuffer;

import io.jstach.jstachio.Output.EncodedOutput;

class DataBufferOutput implements EncodedOutput<RuntimeException> {

	private final DataBuffer buffer;

	private final Charset charset;

	public DataBufferOutput(DataBuffer buffer, Charset charset) {
		super();
		this.buffer = buffer;
		this.charset = charset;
	}

	@Override
	public void append(CharSequence csq) throws RuntimeException {
		buffer.write(csq, charset);
	}

	@Override
	public void write(byte[] bytes) throws RuntimeException {
		buffer.write(bytes);

	}

	@Override
	public void write(byte[] bytes, int off, int len) throws RuntimeException {
		buffer.write(bytes, off, len);
	}

	@Override
	public Charset charset() {
		return this.charset;
	}

	public DataBuffer getBuffer() {
		return buffer;
	}

}
