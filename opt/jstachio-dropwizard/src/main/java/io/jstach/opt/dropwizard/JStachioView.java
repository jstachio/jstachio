package io.jstach.opt.dropwizard;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import io.dropwizard.views.common.View;

/**
 * An interface to mark JStachio dropwizard views
 *
 * @author agentgt
 *
 */
public interface JStachioView {

	/**
	 * The JStache annotated instance
	 * @return model
	 */
	public Object model();

	/**
	 * Creates a dropwizard view from a model object
	 * @param model JStache annotated model
	 * @return dropwizard view
	 */
	@SuppressWarnings("exports")
	public static View of(Object model) {
		return new DefaultJStachioView(model);
	}

	/**
	 * The charset never null
	 * @return utf-8 by default
	 */
	default Charset charset() {
		return StandardCharsets.UTF_8;
	}

}
