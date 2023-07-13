package io.jstach.opt.dropwizard;

import io.dropwizard.views.common.View;
import io.jstach.jstachio.Template;

/**
 * A mixin for generated templates to create dropwizard views.
 *
 * @author agentgt
 * @param <T> model type
 */
public interface ViewableTemplate<T> extends Template<T> {

	/**
	 * Creates a dropwizard view from the model and this template. JStachio will not need
	 * to lookup the template that corresponds to the model.
	 * @param model model that the template can render
	 * @return dropwizard view
	 */
	@SuppressWarnings("exports")
	default View view(T model) {
		return JStachioView.of(model(model));
	}

}
