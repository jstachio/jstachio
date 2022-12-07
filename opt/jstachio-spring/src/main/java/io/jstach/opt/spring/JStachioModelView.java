package io.jstach.opt.spring;

import java.util.Map;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import io.jstach.jstache.JStacheInterfaces;
import io.jstach.jstachio.JStachio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Another way to use JStachio with Spring MVC is to have models implement Springs
 * {@link View} interface. You can enforce that your models implement this interface with
 * {@link JStacheInterfaces}.
 * <p>
 * The model will use the static jstachio singleton that will be the spring one.
 * <p>
 * This approach has pros and cons. It makes your models slightly coupled to Spring MVC
 * but allows you to return different views if say you had to redirect on some inputs
 * ({@link RedirectView}).
 *
 * @author agentgt
 *
 */
public interface JStachioModelView extends View {

	@SuppressWarnings("exports")
	@Override
	default void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		/*
		 * TODO fix this.
		 */
		response.setContentType(contentType());
		try (var w = response.getWriter()) {
			jstachio().execute(this, w);
		}

	}

	/**
	 * Returns the jstachio singleton by default.
	 * @return stachio singleton by default.
	 * @see JStachio#setStaticJStachio(java.util.function.Supplier)
	 */
	default JStachio jstachio() {
		return JStachio.of();
	}

	/**
	 * The HTTP content type header.
	 * @return "text/html; charset=utf-8" by default
	 */
	default String contentType() {
		return "text/html; charset=utf-8";
	}

}
