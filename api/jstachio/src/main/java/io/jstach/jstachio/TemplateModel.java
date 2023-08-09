package io.jstach.jstachio;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import io.jstach.jstache.JStacheInterfaces;
import io.jstach.jstachio.Output.EncodedOutput;
import io.jstach.jstachio.Template.EncodedTemplate;

/**
 * A template and model combined with convenience methods.
 * <p>
 * This tuple like object is useful way to combine the correct template with a model if
 * you have programmatic access to the template. This can be useful to a web framework
 * perhaps as the return type of a web controller and because the template is already
 * located the web framework does not have to re-find the template. An analog in the
 * Spring framework would be <code>ModelAndView</code>.
 * <p>
 * It is purposely not parameterized for ease of use as dealing with generics via
 * reflection can be difficult.
 *
 * @apiNote the interface is purposely sealed for now to see usage and feedback and one
 * can make their own similar version with {@link JStacheInterfaces}.
 * @author agentgt
 *
 */
public sealed interface TemplateModel {

	/**
	 * Template.
	 * @return template to use for execution
	 */
	Template<?> template();

	/**
	 * Model.
	 * @return model to be executed on
	 */
	Object model();

	/**
	 * Renders the passed in model directly to an appendable like output.
	 * @param <A> output type
	 * @param <E> error type
	 * @param output to write to.
	 * @return the passed in output
	 * @throws E if an error occurs while writing to output
	 */
	public <A extends io.jstach.jstachio.Output<E>, E extends Exception> A execute(A output) throws E;

	/**
	 * Renders the passed in model directly to a binary stream possibly leveraging
	 * pre-encoded parts of the template.
	 * @param <A> output type
	 * @param <E> error type
	 * @param output to write to.
	 * @return the passed in output
	 * @throws E if an error occurs while writing to output
	 */
	default <A extends io.jstach.jstachio.Output.EncodedOutput<E>, E extends Exception> A write(A output) throws E {
		return execute(output);
	}

	/**
	 * Convenience method to write directly to an outputstream with the templates
	 * character encoding.
	 * @param outputStream outputStream to write to
	 * @throws IOException if an error happens while writting to the stream.
	 */
	default void write(OutputStream outputStream) throws IOException {
		write(Output.of(outputStream, template().templateCharset()));
	}

	/**
	 * Renders the template to a String.
	 * @return the executed template as a string.
	 */
	default String execute() {
		return execute(Output.of(new StringBuilder())).getBuffer().toString();
	}

	/**
	 * Creates a template model pair.
	 * @param <T> model type
	 * @param template encoded template
	 * @param model model instance
	 * @return the template executable designed for encoded templates.
	 */
	public static <T> TemplateModel of(EncodedTemplate<T> template, T model) {
		return new EncodedTemplateExecutable<>(template, model);
	}

	/**
	 * Creates a template model pair.
	 * @param <T> model type
	 * @param template encoded template
	 * @param model model instance
	 * @return the template executable.
	 */
	public static <T> TemplateModel of(Template<T> template, T model) {
		if (template instanceof EncodedTemplate<T> et) {
			return of(et, model);
		}
		return new DefaultTemplateExecutable<>(template, model);
	}

}

record DefaultTemplateExecutable<T> (Template<T> delegateTemplate, T _model) implements TemplateModel {

	static final String ERROR_MESSAGE = "The model passed into this TemplateModel is not correct";

	@Override
	public <A extends io.jstach.jstachio.Output<E>, E extends Exception> A execute(A output) throws E {
		return delegateTemplate.execute(this._model, output);
	}

	@Override
	public Template<?> template() {
		return delegateTemplate;
	}

	@Override
	public Object model() {
		return Objects.requireNonNull(_model);
	}

}

record EncodedTemplateExecutable<T> (EncodedTemplate<T> delegateTemplate, T _model) implements TemplateModel {

	@Override
	public <A extends io.jstach.jstachio.Output<E>, E extends Exception> A execute(A output) throws E {
		return delegateTemplate.execute(this._model, output);
	}

	@Override
	public <A extends EncodedOutput<E>, E extends Exception> A write(A output) throws E {
		return delegateTemplate.write(_model, output);
	}

	@Override
	public Template<?> template() {
		return delegateTemplate;
	}

	@Override
	public Object model() {
		return Objects.requireNonNull(_model);
	}

}
