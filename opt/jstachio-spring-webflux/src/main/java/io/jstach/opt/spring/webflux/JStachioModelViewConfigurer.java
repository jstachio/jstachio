package io.jstach.opt.spring.webflux;

import java.util.Map;

import org.springframework.web.server.ServerWebExchange;

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
	 * @param model the current model
	 * @param request the current request context
	 */
	void configure(Object page, Map<String, Object> model, ServerWebExchange request);

}
