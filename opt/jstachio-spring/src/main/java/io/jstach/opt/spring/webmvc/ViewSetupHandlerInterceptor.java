package io.jstach.opt.spring.webmvc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A {@link HandlerInterceptor} that automatically applies all
 * {@link JStachioModelViewConfigurer} instances to views before rendering. Also
 * implements {@link WebMvcConfigurer} so that it registers itself when the servlet
 * context starts.
 */
@SuppressWarnings("exports")
public class ViewSetupHandlerInterceptor implements HandlerInterceptor, WebMvcConfigurer {

	private final List<JStachioModelViewConfigurer> configurers = new ArrayList<>();

	private final ApplicationContext context;

	public ViewSetupHandlerInterceptor(ApplicationContext context) {
		for (String name : context.getBeanNamesForType(JStachioModelViewConfigurer.class)) {
			this.configurers.add((JStachioModelViewConfigurer) context.getBean(name));
		}
		this.context = context;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		JStachioModelView view = findView(modelAndView);
		if (view != null) {
			Object page = view.model();
			for (JStachioModelViewConfigurer configurer : configurers) {
				configurer.configure(page, modelAndView.getModel(), request);
			}
		}
	}

	private JStachioModelView findView(ModelAndView modelAndView) {
		if (modelAndView != null) {
			if (modelAndView.getView() instanceof JStachioModelView) {
				return (JStachioModelView) modelAndView.getView();
			}
			if (this.context.getBean(modelAndView.getViewName()) instanceof JStachioModelView view) {
				return view;
			}
		}
		return null;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(this);
	}

}
