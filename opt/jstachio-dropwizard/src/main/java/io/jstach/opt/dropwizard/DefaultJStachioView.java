package io.jstach.opt.dropwizard;

import java.nio.charset.StandardCharsets;

import io.dropwizard.views.common.View;

/**
 * Default view implementation.
 *
 * @author agentgt
 *
 */
public class DefaultJStachioView extends View implements JStachioView {

	private final Object model;

	/**
	 * Creates a view from the given model
	 * @param model jstache annotated model
	 */
	public DefaultJStachioView(Object model) {
		super("JSTACHIO", StandardCharsets.UTF_8);
		this.model = model;
	}

	@Override
	public Object model() {
		return model;
	}

}
