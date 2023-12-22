package io.jstach.jstachio;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstachio.Output.CloseableEncodedOutput;

/**
 * A low level abstraction and implementation detail analogous to {@link Appendable} and
 * {@link DataOutput}.
 *
 * @author agentgt
 * @param <E> the exception type that can happen on output
 */
public interface Output<E extends Exception> {

	/**
	 * Analogous to {@link Appendable#append(CharSequence)}.
	 * @param s unlike appendable always non null.
	 * @throws E if an error happens while writting to the appendable
	 * @apiNote Implementations are required to implement this method.
	 */
	public void append(CharSequence s) throws E;

	/**
	 * Analogous to {@link Appendable#append(CharSequence)} which by default treats the
	 * String as a CharSequence.
	 * @param s unlike appendable always non null.
	 * @throws E if an error happens while writting to the appendable
	 * @apiNote Implementations are required to implement this method.
	 */
	default void append(String s) throws E {
		append((CharSequence) s);
	}

	/**
	 * Analogous to {@link Appendable#append(CharSequence, int, int)}.
	 * @param csq Unlike appendable never null.
	 * @param start start inclusive
	 * @param end end exclusive
	 * @throws E if an error happens while writting to the appendable
	 * @apiNote Implementations are required to implement this method.
	 */
	public void append(CharSequence csq, int start, int end) throws E;

	/**
	 * Appends a character to an appendable.
	 * @param c character
	 * @throws E if an error happens while writting to the appendable
	 * @apiNote Implementations are required to implement this method.
	 */

	public void append(char c) throws E;

	/**
	 * Write a short by using {@link String#valueOf(int)}
	 * @param s short
	 * @throws E if an error happens while writting to the appendable
	 */
	default void append(short s) throws E {
		append(String.valueOf(s));
	}

	/**
	 * Write a int by using {@link String#valueOf(int)}.
	 * <p>
	 * Implementations should override if they want different behavior or able to support
	 * appendables that can write the native type.
	 * @param i int
	 * @throws E if an error happens while writting to the appendable
	 */
	default void append(int i) throws E {
		append(String.valueOf(i));
	}

	/**
	 * Write a long by using {@link String#valueOf(long)}.
	 * <p>
	 * Implementations should override if they want different behavior or able to support
	 * appendables that can write the native type.
	 * @param l long
	 * @throws E if an error happens while writting to the appendable
	 */
	default void append(long l) throws E {
		append(String.valueOf(l));
	}

	/**
	 * Write a long by using {@link String#valueOf(long)}.
	 * <p>
	 * Implementations should override if they want different behavior or able to support
	 * appendables that can write the native type.
	 * @param d double
	 * @throws E if an error happens while writting to the appendable
	 */
	default void append(double d) throws E {
		append(String.valueOf(d));
	}

	/**
	 * Write a long by using {@link String#valueOf(long)}.
	 * <p>
	 * Implementations should override if they want different behavior or able to support
	 * appendables that can write the native type.
	 * @param b boolean
	 * @throws E if an error happens while writting to the appendable
	 */
	default void append(boolean b) throws E {
		append(String.valueOf(b));
	}

	/**
	 * Adapts an {@link OutputStream} as an {@link Output}.
	 * @param a the OutputStream to be wrapped.
	 * @param charset the encoding to use
	 * @return outputstream output
	 */
	public static EncodedOutput<IOException> of(OutputStream a, Charset charset) {
		return new OutputStreamOutput(a, charset);
	}

	/**
	 * Adapts an {@link Appendable} as an {@link Output}.
	 * @param a the appendable to be wrapped.
	 * @return string based output
	 */
	public static Output<IOException> of(Appendable a) {
		return new AppendableOutput(a);
	}

	/**
	 * Adapts a {@link StringBuilder} as an {@link Output}.
	 * @param a the StringBuilder to be wrapped.
	 * @return string based output
	 */
	public static StringOutput of(StringBuilder a) {
		return new StringOutput(a);
	}

	/**
	 * Converts the output to an appendable unless it already is one.
	 * @return adapted appendable of this output.
	 */
	default Appendable toAppendable() {
		if (this instanceof Appendable a) {
			return a;
		}
		return new OutputAppendable(this);
	}

	/**
	 * A specialized Output designed for pre-encoded templates that have already encoded
	 * byte arrays to be used directly.
	 * <p>
	 * Most template engines traditionally write to an Appendable and as they write to the
	 * appendable in a web framework the output is eventually converted to bytes in almost
	 * always <code>UTF-8</code> format. Given that a large majority of templates is
	 * static text this encoding can be done apriori which saves some processing time
	 * especially if the text contains any non latin1 characters.
	 *
	 * @author agentgt
	 * @param <E> the exception type
	 */
	public interface EncodedOutput<E extends Exception> extends Output<E> {

		/**
		 * Analogous to {@link OutputStream#write(byte[])}. Implementations should not
		 * alter the byte array.
		 * @param bytes already encoded bytes
		 * @throws E if an error happens
		 */
		public void write(byte[] bytes) throws E;

		/**
		 * Analogous to {@link OutputStream#write(byte[], int, int)}. Generated templates
		 * do not call this method as great care as to be taken to preserve the encoding.
		 * It is only provided in the case of future found optimizations and is not
		 * currently required.
		 * <p>
		 * The default implementation creates an array copies the data and then calls
		 * {@link #write(byte[])}.
		 * @param bytes already encoded bytes
		 * @param off offset
		 * @param len length to copy
		 * @throws E if an error happens
		 */
		default void write(byte[] bytes, int off, int len) throws E {
			byte[] dest = new byte[len];
			System.arraycopy(bytes, off, dest, 0, len);
			write(dest);
		}

		@Override
		default void append(char c) throws E {
			append(String.valueOf(c));
		}

		@Override
		default void append(String s) throws E {
			write(s.getBytes(charset()));
		}

		@Override
		default void append(CharSequence csq, int start, int end) throws E {
			append(csq.subSequence(start, end).toString());
		}

		/**
		 * The charset that the encoded output <em>should</em> be.
		 * @return expected charset
		 */
		Charset charset();

		/**
		 * Adapts an {@link OutputStream} as an {@link EncodedOutput}. The resulting
		 * output can be closed and will close the passed in OutputStream.
		 * @param a the OutputStream to be wrapped.
		 * @param charset the encoding to use
		 * @return outputstream output
		 */
		public static CloseableEncodedOutput<IOException> of(OutputStream a, Charset charset) {
			return new OutputStreamOutput(a, charset);
		}

	}

	/**
	 * An encoded output that can be closed. This maybe to close downstream outputstreams
	 * or to signify ready for reuse or to clear buffers.
	 *
	 * @author agent
	 * @param <E> error on close
	 */
	public interface CloseableEncodedOutput<E extends Exception> extends EncodedOutput<E>, AutoCloseable {

		@Override
		public void close() throws E;

	}

	/**
	 * String Builder based output.
	 *
	 * @author agentgt
	 *
	 */
	public class StringOutput implements Output<RuntimeException> {

		private final StringBuilder buffer;

		/**
		 * Create using supplied StringBuilder.
		 * @param buffer never null.
		 */
		public StringOutput(StringBuilder buffer) {
			super();
			this.buffer = buffer;
		}

		@Override
		public void append(CharSequence s) {
			buffer.append(s);
		}

		@Override
		public void append(String s) {
			buffer.append(s);
		}

		@Override
		public void append(CharSequence csq, int start, int end) {
			buffer.append(csq, start, end);

		}

		@Override
		public void append(char c) {
			buffer.append(c);

		}

		@Override
		public String toString() {
			return buffer.toString();
		}

		@Override
		public void append(boolean b) throws RuntimeException {
			buffer.append(b);
		}

		@Override
		public void append(double d) throws RuntimeException {
			buffer.append(d);
		}

		@Override
		public void append(int i) throws RuntimeException {
			buffer.append(i);
		}

		@Override
		public void append(long l) throws RuntimeException {
			buffer.append(l);
		}

		@Override
		public void append(short s) throws RuntimeException {
			buffer.append(s);
		}

		/**
		 * The buffer that has been wrapped.
		 * @return the wrapped builder
		 */
		public StringBuilder getBuffer() {
			return buffer;
		}

	}

}

class OutputAppendable implements Appendable {

	private final Output<?> output;

	public OutputAppendable(Output<?> output) {
		super();
		this.output = output;
	}

	@Override
	public Appendable append(@Nullable CharSequence csq) throws IOException {
		try {
			output.append(csq == null ? "null" : csq);
			return this;
		}
		catch (Exception e) {
			if (e instanceof IOException ioe) {
				throw ioe;
			}
			throw new IOException(e);
		}
	}

	@Override
	public Appendable append(@Nullable CharSequence csq, int start, int end) throws IOException {
		try {
			output.append(csq == null ? " null " : csq, start, end);
			return this;
		}
		catch (Exception e) {
			if (e instanceof IOException ioe) {
				throw ioe;
			}
			throw new IOException(e);
		}
	}

	@Override
	public Appendable append(char c) throws IOException {
		try {
			output.append(c);
			return this;
		}
		catch (Exception e) {
			if (e instanceof IOException ioe) {
				throw ioe;
			}
			throw new IOException(e);
		}
	}

}

class AppendableOutput implements Output<IOException> {

	private final Appendable appendable;

	public AppendableOutput(Appendable appendable) {
		super();
		this.appendable = appendable;
	}

	@Override
	public void append(CharSequence s) throws IOException {
		appendable.append(s);
	}

	@Override
	public void append(CharSequence csq, int start, int end) throws IOException {
		appendable.append(csq, start, end);

	}

	@Override
	public void append(char c) throws IOException {
		appendable.append(c);

	}

}

class OutputStreamOutput implements CloseableEncodedOutput<IOException> {

	private final OutputStream outputStream;

	private final Charset charset;

	public OutputStreamOutput(OutputStream outputStream, Charset charset) {
		super();
		this.outputStream = outputStream;
		this.charset = charset;
	}

	@Override
	public void write(byte[] b) throws IOException {
		outputStream.write(b);
	}

	@Override
	public void write(byte[] bytes, int off, int len) throws IOException {
		outputStream.write(bytes, off, len);
	}

	@Override
	public void append(CharSequence csq) throws IOException {
		append(csq.toString());
	}

	@Override
	public void append(String s) throws IOException {
		write(s.getBytes(charset));
	}

	@Override
	public Charset charset() {
		return charset;
	}

	@Override
	public void close() throws IOException {
		outputStream.close();
	}

}
