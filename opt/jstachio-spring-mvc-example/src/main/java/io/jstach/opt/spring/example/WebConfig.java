package io.jstach.opt.spring.example;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.jstach.jstachio.JStachio;
import io.jstach.opt.spring.web.JStachioHttpMessageConverter;
import io.jstach.opt.spring.webmvc.JStachioModelViewConfigurer;
import io.jstach.opt.spring.webmvc.ViewSetupHandlerInterceptor;

/**
 * Configures MVC using {@link JStachioHttpMessageConverter} to allow returning models
 * which will be rendered using JStachio runtime.
 *
 * @see JStachioHttpMessageConverter
 * @author agentgt
 * @author dsyer
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final JStachio jstachio;

	/**
	 * Configures based on the jstachio found by spring
	 * @param jstachio the found jstachio
	 */
	@Autowired
	public WebConfig(JStachio jstachio) {
		this.jstachio = jstachio;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new JStachioHttpMessageConverter(jstachio));
	}

	/**
	 * Configures an interceptor for before rendering logic useful for adding additional
	 * data to JStache models.
	 * @param context searched for {@link JStachioModelViewConfigurer}s.
	 * @return interceptor that will automatically be added to the web context.
	 */
	@Bean
	@SuppressWarnings("exports")
	public ViewSetupHandlerInterceptor viewSetupHandlerInterceptor(ApplicationContext context) {
		return new ViewSetupHandlerInterceptor(context);
	}

}
