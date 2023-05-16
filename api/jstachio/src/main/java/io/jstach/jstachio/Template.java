package io.jstach.jstachio;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheFlags;

/**
 * A JStachio Template is a renderer that has template meta data.
 * <p>
 * Generated code implements this interface.
 * <p>
 * While many of the methods allow passing in custom Escapers care must be taken to choose
 * a proper escaper that supports the original media type and charset of the template.
 * There is currently no runtime checking that the escaper supports the template's media
 * type and charset.
 *
 * @author agentgt
 * @param <T> the model type
 */
public interface Template<T> extends Renderer<T>, TemplateInfo {

	/**
	 * Renders the passed in model.
	 * @param model a model assumed never to be <code>null</code>.
	 * @param appendable the appendable to write to.
	 * @throws IOException if there is an error writing to the appendable
	 */
	default void execute(T model, Appendable appendable) throws IOException {
		execute(model, appendable, Formatter.of(templateFormatter()), Escaper.of(templateEscaper()));
	}

	/**
	 * Renders the passed in model.
	 * @param model a model assumed never to be <code>null</code>.
	 * @param a appendable to write to.
	 * @param formatter formats variables before they are passed to the escaper
	 * @param escaper used to write escaped variables
	 * @throws IOException if an error occurs while writing to the appendable
	 */
	public void execute(T model, //
			Appendable a, //
			Formatter formatter, //
			Escaper escaper) throws IOException;

	/**
	 * Renders the passed in model.
	 * @param model a model assumed never to be <code>null</code>.
	 * @param a appendable to write to.
	 * @param formatter formats variables before they are passed to the escaper
	 * @param escaper used to write escaped variables
	 * @throws IOException if an error occurs while writing to the appendable
	 */
	@SuppressWarnings("exports")
	default void execute(T model, //
			Appendable a, //
			Function<@Nullable Object, String> formatter, //
			Function<String, String> escaper) throws IOException {
		execute(model, a, Formatter.of(formatter), Escaper.of(escaper));
	}

	/**
	 * Renders the passed in model directly to a binary stream using the
	 * {@link #templateCharset()} for encoding. If the template is
	 * {@linkplain JStacheFlags.Flag#PRE_ENCODE pre-encoded} the pre-encoded parts of the
	 * template will be written to the stream for performance otherwise an unbuffered
	 * {@link OutputStreamWriter} will be used.
	 * @param model a model assumed never to be <code>null</code>.
	 * @param outputStream to write to.
	 * @throws IOException if an error occurs while writing to the outputStream
	 * @apiNote The stream will not be closed or flushed by this call.
	 * @see EncodedTemplate
	 */
	default void write(T model, //
			OutputStream outputStream) throws IOException {
		OutputStreamWriter ow = new OutputStreamWriter(outputStream, Charset.forName(templateCharset()));
		execute(model, ow);
	}

	/**
	 * <strong>EXPERIMENTAL</strong> support of pre-encoded templates that have the static
	 * parts of the template already encoded into bytes. To generate templates that
	 * support this interface and are pre-encoded add {@link JStacheFlags.Flag#PRE_ENCODE}
	 * to the {@linkplain JStacheFlags template flags}.
	 *
	 * <p>
	 * This interface is to support
	 * <a href="https://github.com/fizzed/rocker#near-zero-copy-rendering"> Rocker style
	 * near zero-copy rendering</a> where the static template parts are stored as byte
	 * arrays.
	 * <p>
	 * The passed in {@link OutputStream} <em>will only have
	 * {@link OutputStream#write(byte[])} called</em> thus an array or list of byte arrays
	 * can be accumulated to support almost zero-copy. <strong> Consequently absolutely no
	 * mutation of the byte arrays should happen as they could be reusable static parts of
	 * the template! </strong>
	 * <p>
	 * <em>Overall it is recommended that you do not use this interface unless you have an
	 * intimate knowledge of how your platform buffers data and have byte like access as
	 * current JMH benchmarking indicates that
	 * {@link String#getBytes(java.nio.charset.Charset)} is generally much faster for raw
	 * byte conversion albeit at the possible cost of increased memory. One should peform
	 * their own benchmarking to confirm using this interface is worth it. </em>
	 *
	 * @author agentgt
	 * @param <T> the model type
	 * @apiNote The passed in {@link OutputStream} <em>will only have
	 * {@link OutputStream#write(byte[])} called</em> and no mutation of the passed in
	 * byte array should happen downstream.
	 * @see JStacheFlags.Flag#PRE_ENCODE
	 */
	public interface EncodedTemplate<T> extends Template<T> {

		/**
		 * Renders the passed in model directly to a binary stream leveraging pre-encoded
		 * parts of the template. This <em>may</em> improve performance when rendering
		 * UTF-8 to an OutputStream as some of the encoding is done in advance. Because
		 * the encoding is done statically you cannot pass the charset in. The chosen
		 * charset comes from {@link JStacheConfig#charset()}.
		 * @param model a model assumed never to be <code>null</code>.
		 * @param outputStream to write to.
		 * @throws IOException if an error occurs while writing to the outputStream
		 * @apiNote The stream will not be closed or flushed by this call.
		 */
		default void write(T model, //
				OutputStream outputStream) throws IOException {
			write(model, outputStream, Formatter.of(templateFormatter()), Escaper.of(templateEscaper()));
		}

		/**
		 * Renders the passed in model directly to a binary stream leveraging pre-encoded
		 * parts of the template. This <em>may</em> improve performance when rendering
		 * UTF-8 to an OutputStream as some of the encoding is done in advance. Because
		 * the encoding is done statically you cannot pass the charset in. The chosen
		 * charset comes from {@link JStacheConfig#charset()}.
		 * <p>
		 * For performance reasons the passed in escaper is not checked if it supports the
		 * encoding so care must be taken to use an escaper that supports the static
		 * {@link #templateCharset()}.
		 * @param model a model assumed never to be <code>null</code>.
		 * @param outputStream to write to.
		 * @param formatter formats variables before they are passed to the escaper
		 * @param escaper used to write escaped variables
		 * @throws IOException if an error occurs while writing to the outputStream
		 * @apiNote The stream will not be closed or flushed by this call.
		 */
		public void write(T model, //
				OutputStream outputStream, //
				Formatter formatter, //
				Escaper escaper) throws IOException;

	}

}
