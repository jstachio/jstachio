package io.jstach.spi;

import java.io.IOException;
import java.util.List;

import io.jstach.Renderer;
import io.jstach.TemplateInfo;

/**
 * Advises or filters a previously applied template.
 *
 * @author agentgt
 *
 */
public interface JStacheFilter {

	/**
	 * Advises or filters a previously applied template and model like a filter chain.
	 * @param <T> type that the renderer can handle
	 * @param template info about the template
	 * @param context the root model
	 * @param previous the function returned early in the chain.
	 * @return an advised render function or often the previous render function if no
	 * advise is needed.
	 */
	<T> Renderer<? super T> filter( //
			TemplateInfo template, //
			T context, //
			Renderer<? super T> previous);

	/**
	 * Applies filter with previous filter broken unless the template is a renderer.
	 * @param <T> type that the renderer can handle
	 * @param template info about the template
	 * @param context the root model
	 * @return an advised render function or often the previous render function if no
	 * advise is needed.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	default <T> Renderer<? super T> filter( //
			TemplateInfo template, //
			T context) {
		Renderer previous = BrokenRenderer.INSTANCE;
		if (template instanceof Renderer r && template.supportsType(context.getClass())) {
			previous = r;
		}
		return filter(template, context, previous);
	}

}

enum BrokenRenderer implements Renderer<Object> {

	INSTANCE;

	@Override
	public void execute(Object model, Appendable appendable) throws IOException {
		throw new IOException();
	}

	@Override
	public boolean isBroken() {
		return true;
	}

}

class FilterChain implements JStacheFilter {

	private final List<JStacheFilter> filters;

	public FilterChain(List<JStacheFilter> filters) {
		super();
		this.filters = filters;
	}

	@Override
	public <T> Renderer<? super T> filter(TemplateInfo template, T context, Renderer<? super T> previous) {
		var current = previous;
		for (var f : filters) {
			current = f.filter(template, context, current);
		}
		return current;
	}

}