package io.jstach.opt.spring.boot.webmvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.jstach.opt.spring.web.JStachioHttpMessageConverter;
import io.jstach.opt.spring.webmvc.JStachioModelViewConfigurer;
import io.jstach.opt.spring.webmvc.ViewSetupHandlerInterceptor;

/**
 * AutoConfiguration for JStachio runtime.
 *
 * @see JStachioHttpMessageConverter
 * @author agentgt
 * @author dsyer
 */
@AutoConfiguration
@AutoConfigureAfter(value = { JStachioAutoConfiguration.class })
public class JStachioWebMvcAutoConfiguration implements WebMvcConfigurer {

	private final JStachioHttpMessageConverter messageConverter;

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
