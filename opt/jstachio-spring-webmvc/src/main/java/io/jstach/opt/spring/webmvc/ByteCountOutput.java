package io.jstach.opt.spring.webmvc;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import io.jstach.jstachio.Output;

/*
 * TODO incorporate this with Output.of
 */
class ByteCountOutput implements Output.EncodedOutput<IOException> {

	private final OutputStream outputStream;

	private final Charset charset;

	private long size = 0;

	public ByteCountOutput(OutputStream outputStream, Charset charset) {
		super();
		this.outputStream = outputStream;
		this.charset = charset;
	}

	@Override
	public void write(byte[] b) throws IOException {
		size += b.length;
		outputStream.write(b);
	}

	@Override
	public void write(byte[] bytes, int off, int len) throws IOException {
		size += len;
		outputStream.write(bytes, off, len);
	}
	
	@Override
		public void append(
				String s)
				throws IOException {
		var bytes = s.getBytes(this.charset);
		size += bytes.length;
		outputStream.write(bytes);
	}

	@Override
	public Charset charset() {
		return charset;
	}

	public long size() {
		return size;
	}

	@Override
	public void append(
			CharSequence s)
			throws IOException {
		append(s.toString());
	}

}
