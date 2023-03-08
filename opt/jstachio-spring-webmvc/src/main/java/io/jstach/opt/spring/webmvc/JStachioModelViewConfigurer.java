package io.jstach.opt.spring.webmvc;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

/**
 * User can provide instances in the application context and they will be applied to each
 * {@link JStachioModelView} instance before rendering.
 * 
 * @author dsyer
 */
public interface JStachioModelViewConfigurer {

	/**
	 * Configures a JStache model with the current request and traditional Spring model
	 * before being rendered.
	 * <p>
	 * This is useful to add specific request meta data like CSRF token to the JStache
	 * model so that the Controllers do not have to worry about doing that.
	 * @param page the current {@link io.jstach.jstache.JStache} model
	 * @param model the current Spring MVC model
	 * @param request the current servlet request
	 */
	@SuppressWarnings("exports")
	void configure(Object page, Map<String, ?> model, HttpServletRequest request);

}
