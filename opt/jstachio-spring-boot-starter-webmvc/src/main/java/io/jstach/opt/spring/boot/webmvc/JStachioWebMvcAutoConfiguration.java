package io.jstach.opt.spring.boot.webmvc;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.jstach.opt.spring.web.JStachioHttpMessageConverter;
import io.jstach.opt.spring.webmvc.ViewResolvingHandlerInterceptor;
import io.jstach.opt.spring.webmvc.ViewSetupHandlerInterceptor;

/**
 * MVC AutoConfiguration for JStachio runtime.
 *
 * @see JStachioHttpMessageConverter
 * @author agentgt
 * @author dsyer
 */
@AutoConfiguration
@AutoConfigureAfter(value = { JStachioAutoConfiguration.class })
public class JStachioWebMvcAutoConfiguration implements WebMvcConfigurer, ApplicationContextAware {

	private final JStachioHttpMessageConverter messageConverter;

	private ApplicationContext context;

	/**
	 * Configures based on the jstachio found by spring
	 * @param messageConverter jstachio powered message converter
	 */
	@Autowired
	public JStachioWebMvcAutoConfiguration(JStachioHttpMessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(0, this.messageConverter);
	}

	@Override
	@SuppressWarnings("exports")
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new ViewSetupHandlerInterceptor(context));
		registry.addInterceptor(new ViewResolvingHandlerInterceptor());
	}

	@Override
	@SuppressWarnings("exports")
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}

}
