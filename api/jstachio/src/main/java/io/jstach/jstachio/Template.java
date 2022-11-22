package io.jstach.jstachio;

import java.io.IOException;

import io.jstach.jstachio.spi.JStachioFilter.FilterChain;

/**
 * A JStachio Template is a renderer that has template meta data.
 * <p>
 * Generated code implements this interface.
 *
 * @author agentgt
 * @param <T> the model type
 */
public interface Template<T> extends Renderer<T>, TemplateInfo, FilterChain {

	/* TODO remove implements filter chain and add to generated code directly */

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

	@SuppressWarnings("unchecked")
	@Override
	default void process(Object model, Appendable appendable) throws IOException {
		execute((T) model, appendable);
	}

	@Override
	default boolean isBroken(Object model) {
		return !supportsType(model.getClass());
	}

}
