package io.jstach.spi;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;

import io.jstach.Renderer;
import io.jstach.TemplateInfo;

/**
 * Adapts a filter to ease adding a fallback template engine such as JMustache.
 *
 * @author agentgt
 */
public abstract class AbstractJStacheEngine implements JStacheFilter, JStacheServices {

	/**
	 * Do nothing constructor
	 */
	public AbstractJStacheEngine() {
	}

	@Override
	public final <T> Renderer<? super T> filter(TemplateInfo template, T context, Renderer<? super T> previous) {
		return (model, a) -> {
			boolean answer = execute(context, a, template, previous.isBroken());
			if (answer) {
				return;
			}
			previous.execute(model, a);
		};

	}

	/**
	 * {@inheritDoc} The implementation is a filter and provides itself.
	 */
	@Override
	public final @NonNull JStacheFilter provideFilter() {
		return this;
	}

	/**
	 * Execute the template engine. If the engine chooses not to participate
	 * <code>false</code> should be returned.
	 * @param context the model
	 * @param a the appendable to write to
	 * @param template template info
	 * @param broken whether or no the previous filter (usually jstachio itself) is
	 * broken.
	 * @return <code>true</code> if the engine has written to the appendable
	 * @throws IOException error writing to the appendable
	 */
	protected abstract boolean execute(Object context, Appendable a, TemplateInfo template, boolean broken)
			throws IOException;

}