package io.jstach.opt.spring.webmvc;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.jstach.jstache.JStache;
import io.jstach.jstachio.JStachio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A {@link HandlerInterceptor} to automatically resolve a {@link Controller} method
 * return value that is a {@link JStache} model into a {@link JStachioModelView}.
 *
 * @author dsyer
 */
@SuppressWarnings("exports")
public class ViewResolvingHandlerInterceptor implements HandlerInterceptor, WebMvcConfigurer {

	private final JStachio jstachio;

	/**
	 * Spring will inject jstachio
	 * @param jstachio jstachio instance found by spring.
	 */
	public ViewResolvingHandlerInterceptor(JStachio jstachio) {
		super();
		this.jstachio = jstachio;
	}

	/**
	 * {@inheritDoc} If the model contains an attribute that is a {@link JStache} model,
	 * in particular if it has been the result of a {@link Controller} method, then it
	 * will be automatically converted into a {@link JStachioModelView}.
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		if (modelAndView == null || !HandlerMethod.class.isInstance(handler)) {
			return;
		}

		HandlerMethod method = (HandlerMethod) handler;

		View view = view(method.getReturnType().getParameterType(), modelAndView.getModel());
		if (view != null) {
			modelAndView.setView(view);
		}
	}

	private View view(Class<?> modelClass, Map<String, Object> model) {
		if (!jstachio.supportsType(modelClass)) {
			return null;
		}
		Object value = attribute(modelClass, model);
		if (value == null) {
			return null;
		}
		return JStachioModelView.of(value, JStachioModelView.DEFAULT_MEDIA_TYPE, jstachio);
	}

	private Object attribute(Class<?> modelClass, Map<String, Object> model) {
		String name = ClassUtils.getShortNameAsProperty(modelClass);
		if (ClassUtils.isAssignableValue(modelClass, model.get(name))) {
			return model.get(name);
		}
		for (String key : model.keySet()) {
			if (ClassUtils.isAssignableValue(modelClass, model.get(key))) {
				return model.get(key);
			}
		}
		return null;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(this);
	}

}
