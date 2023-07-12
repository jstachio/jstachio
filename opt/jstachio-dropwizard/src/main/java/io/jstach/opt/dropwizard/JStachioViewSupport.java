package io.jstach.opt.dropwizard;

import io.dropwizard.views.common.View;

/**
 * Have JStache models implement this interface for easier support to generate views from
 * models
 *
 * @author agentgt
 */
public interface JStachioViewSupport {

	/**
	 * Creates Dropwizard view from this model
	 * @return dropwizard view
	 */
	@SuppressWarnings("exports")
	default View toView() {
		return JStachioView.of(this);
	}

}
