package io.jstach.jstachio;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStacheConfig;

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
	 * Renders the passed in model directly to a binary stream leveraging pre-encoded
	 * parts of the template. This may improve performance when rendering UTF-8 to an
	 * OutputStream as some of the encoding is done in advance. Because the encoding is
	 * done statically you cannot pass the charset in. The chosen charset comes from
	 * {@link JStacheConfig#charset()}.
	 * @param model a model assumed never to be <code>null</code>.
	 * @param outputStream to write to.
	 * @throws IOException if an error occurs while writing to the appendable
	 * @apiNote The stream will not be closed or flushed by this call.
	 */
	default void write(T model, //
			OutputStream outputStream) throws IOException {
		write(model, outputStream, Formatter.of(templateFormatter()), Escaper.of(templateEscaper()));
	}

	/**
	 * Renders the passed in model directly to a binary stream leveraging pre-encoded
	 * parts of the template. This may improve performance when rendering UTF-8 to an
	 * OutputStream as some of the encoding is done in advance. Because the encoding is
	 * done statically you cannot pass the charset in. The chosen charset comes from
	 * {@link JStacheConfig#charset()}.
	 * @param model a model assumed never to be <code>null</code>.
	 * @param outputStream to write to.
	 * @param formatter formats variables before they are passed to the escaper
	 * @param escaper used to write escaped variables
	 * @throws IOException if an error occurs while writing to the appendable
	 * @apiNote The stream will not be closed or flushed by this call.
	 */
	public void write(T model, //
			OutputStream outputStream, //
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
	 * Return the model class (root context class annotated with JStache) that generated
	 * this template.
	 * @return model class
	 */
	public Class<?> modelClass();

}
