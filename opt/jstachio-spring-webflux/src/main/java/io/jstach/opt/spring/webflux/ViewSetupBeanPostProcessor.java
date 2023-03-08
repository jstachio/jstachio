package io.jstach.opt.spring.webflux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.reactive.result.view.AbstractView;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.reactive.result.view.RequestContext;
import org.springframework.web.reactive.result.view.RequestDataValueProcessor;
import org.springframework.web.reactive.result.view.ViewResolutionResultHandler;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * A {@link BeanPostProcessor} that it registers a {@link HandlerResultHandler} that
 * automatically applies all {@link JStachioModelViewConfigurer} instances to views before
 * rendering.
 */
public class ViewSetupBeanPostProcessor implements BeanPostProcessor {

	private final ApplicationContext context;

	/**
	 * Constructor for Spring to inject the application context.
	 * @param context supplied by spring used to look for
	 * {@link JStachioModelViewConfigurer}s.
	 */
	public ViewSetupBeanPostProcessor(ApplicationContext context) {
		this.context = context;
	}

	/**
	 * Look for a {@link ViewResolutionResultHandler} and replace it with a wrapper that
	 * configures {@link JStachioModelView} instance using the
	 * {@link JStachioModelViewConfigurer}s in the current context.
	 * @param bean the bean that is being created
	 * @param beanName the name of the bean
	 * @return Object a bean wrapped with {@link ViewSetupResultHandler} if needed
	 * @throws BeansException in case of errors
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof ViewResolutionResultHandler handler) {
			return new ViewSetupResultHandler(this.context, handler);
		}
		return bean;
	}

	class ViewSetupResultHandler implements HandlerResultHandler, Ordered {

		private final ViewResolutionResultHandler delegate;

		private final List<JStachioModelViewConfigurer> configurers = new ArrayList<>();

		private final ApplicationContext context;

		ViewSetupResultHandler(ApplicationContext context, ViewResolutionResultHandler handler) {
			for (String name : context.getBeanNamesForType(JStachioModelViewConfigurer.class)) {
				this.configurers.add((JStachioModelViewConfigurer) context.getBean(name));
			}
			this.delegate = handler;
			this.context = context;
		}

		@Override
		public int getOrder() {
			return this.delegate.getOrder();
		}

		@Override
		public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
			JStachioModelView view = findView(result.getReturnValue());
			if (view != null) {
				for (JStachioModelViewConfigurer configurer : configurers) {
					configurer.configure(view.model(), result.getModel().asMap(), exchange);
				}
			}
			return this.delegate.handleResult(exchange, result);
		}

		protected RequestContext createRequestContext(ServerWebExchange exchange, Map<String, Object> model) {
			return new RequestContext(exchange, model, this.context, getRequestDataValueProcessor());
		}

		@Nullable
		protected RequestDataValueProcessor getRequestDataValueProcessor() {
			ApplicationContext context = this.context;
			if (context != null && context.containsBean(AbstractView.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME)) {
				return context.getBean(AbstractView.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME,
						RequestDataValueProcessor.class);
			}
			return null;
		}

		private JStachioModelView findView(Object view) {
			if (view != null) {
				if (view instanceof Rendering rendering) {
					view = rendering.view();
				}
				if (view instanceof JStachioModelView jview) {
					return jview;
				}
				if (view instanceof String viewName) {
					if (this.context.getBean(viewName) instanceof JStachioModelView jview) {
						return jview;
					}
				}
			}
			return null;
		}

		@Override
		public boolean supports(HandlerResult result) {
			return this.delegate.supports(result);
		}

	}

}
