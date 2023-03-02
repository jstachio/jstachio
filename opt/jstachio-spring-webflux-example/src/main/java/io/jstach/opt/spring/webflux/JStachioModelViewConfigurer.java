package io.jstach.opt.spring.webflux;

import java.util.Map;

import org.springframework.web.server.ServerWebExchange;

/**
 * User can provide instances in the application context and they will be applied to each
 * {@link JStachioModelView} instance before rendering.
 */
public interface JStachioModelViewConfigurer {

	/**
	 * @param page the current {@link io.jstach.jstache.JStache} model
	 * @param model the current model
	 * @param request the current request context
	 */
	void configure(Object page, Map<String, Object> model, ServerWebExchange request);

}
