package io.jstach.opt.spring.webmvc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
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
 *
 * @author dsyer
 */
@SuppressWarnings("exports")
public class ViewSetupHandlerInterceptor implements HandlerInterceptor, WebMvcConfigurer {

	private final List<JStachioModelViewConfigurer> configurers = new ArrayList<>();

	private final ApplicationContext context;

	/**
	 * Injected context will be searched for {@link JStachioModelViewConfigurer}
	 * @param context autowired.
	 */
	public ViewSetupHandlerInterceptor(ApplicationContext context) {
		for (String name : context.getBeanNamesForType(JStachioModelViewConfigurer.class)) {
			this.configurers.add((JStachioModelViewConfigurer) context.getBean(name));
		}
		this.context = context;
	}

	/**
	 * {@inheritDoc} If the modelAndView is a {@link JStachioModelView} then it will be
	 * configured with all of the found {@link JStachioModelViewConfigurer} in the
	 * application context.
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		@Nullable
		JStachioModelView view = findView(modelAndView);
		if (view != null) {
			Object page = view.model();
			for (JStachioModelViewConfigurer configurer : configurers) {
				configurer.configure(page, modelAndView.getModel(), request);
			}
		}
	}

	private @Nullable JStachioModelView findView(ModelAndView modelAndView) {
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
