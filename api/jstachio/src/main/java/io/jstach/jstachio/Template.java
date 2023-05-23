package io.jstach.jstachio;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheFlags;
import io.jstach.jstache.JStacheType;

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
	 * Renders the passed in model to an appendable like output.
	 * @param <A> output type
	 * @param <E> error type
	 * @param model a model assumed never to be <code>null</code>.
	 * @param appendable the appendable to write to.
	 * @throws E if there is an error writing to the output
	 * @apiNote if the eventual output is to be bytes use
	 * {@link #write(Object, io.jstach.jstachio.Output.EncodedOutput)} as it will leverage
	 * pre-encoding if the template has it.
	 */
	public <A extends Output<E>, E extends Exception> A execute(T model, A appendable) throws E;

	/**
	 * Renders the passed in model directly to a binary stream possibly leveraging
	 * pre-encoded parts of the template.
	 * @param <A> output type
	 * @param <E> error type
	 * @param model a model assumed never to be <code>null</code>.
	 * @param output to write to.
	 * @return the passed in output for convenience
	 * @throws E if an error occurs while writing to output
	 * @apiNote for performance and code generation reasons templates do not do validation
	 * that the encoded output has the correct charset.
	 */
	default <A extends io.jstach.jstachio.Output.EncodedOutput<E>, E extends Exception> A write( //
			T model, //
			A output) throws E {
		return execute(model, output);
	}

	/**
	 * Renders the passed in model directly to a binary stream using the
	 * {@link #templateCharset()} for encoding. If the template is pre-encoded the
	 * pre-encoded parts of the template will be written to the stream for performance
	 * otherwise an {@link OutputStreamWriter} will wrap the outputstream.
	 * @param model a model assumed never to be <code>null</code>.
	 * @param outputStream to write to.
	 * @throws IOException if an error occurs while writing to the outputStream
	 * @apiNote The stream will not be closed but might be flushed by this call.
	 * @see EncodedTemplate
	 */
	default void write(T model, //
			OutputStream outputStream) throws IOException {
		OutputStreamWriter ow = new OutputStreamWriter(outputStream, templateCharset());
		execute(model, ow);
		ow.flush();
	}

	/**
	 * Creates a template model pair.
	 * @param model never <code>null</code> model.
	 * @return executable template model pair.
	 */
	default TemplateExecutable model(T model) {
		return TemplateExecutable.of(this, model);
	}

	/**
	 * <strong>EXPERIMENTAL</strong> support of pre-encoded templates that have the static
	 * parts of the template already encoded into bytes. By default all
	 * {@link JStacheType#JSTACHIO} templates that are generated are of this type. To
	 * disable see {@link JStacheFlags.Flag#PRE_ENCODE_DISABLE}.
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
	 * @see JStacheFlags.Flag#PRE_ENCODE_DISABLE
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
		@Override
		default void write(T model, //
				OutputStream outputStream) throws IOException {
			write(model, Output.of(outputStream, templateCharset()));
		}

		/**
		 * Renders the passed in model directly to a binary stream leveraging pre-encoded
		 * parts of the template. This <em>may</em> improve performance when rendering
		 * UTF-8 to an OutputStream as some of the encoding is done in advance. Because
		 * the encoding is done statically you cannot pass the charset in. The chosen
		 * charset comes from {@link JStacheConfig#charset()}.
		 * @param <A> output type
		 * @param <E> error type
		 * @param model a model assumed never to be <code>null</code>.
		 * @param output to write to.
		 * @return the passed in output for convenience
		 * @throws E if an error occurs while writing to output
		 */
		public <A extends io.jstach.jstachio.Output.EncodedOutput<E>, E extends Exception> A write( //
				T model, //
				A output) throws E;

		@Override
		default TemplateExecutable model(T model) {
			return TemplateExecutable.of(this, model);
		}

	}

}
