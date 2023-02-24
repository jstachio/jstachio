package io.jstach.opt.spring.webmvc;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

/**
 * User can provide instances in the application context and they will be applied to each
 * {@link JStachioModelView} instance before rendering.
 */
public interface JStachioModelViewConfigurer {

	/**
	 * @param page the current {@link io.jstach.jstache.JStache} model
	 * @param model the current Spring MVC model
	 * @param request the current servlet request
	 */
	@SuppressWarnings("exports")
	void configure(Object page, Map<String, ?> model, HttpServletRequest request);

}
