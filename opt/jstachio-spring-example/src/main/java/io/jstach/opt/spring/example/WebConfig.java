package io.jstach.opt.spring.example;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.jstach.jstachio.JStachio;
import io.jstach.opt.spring.JStachioHttpMessageConverter;

/**
 * Configures MVC using {@link JStachioHttpMessageConverter} to allow returning models
 * which will be rendered using JStachio runtime.
 *
 * @author agentgt
 * @see JStachioHttpMessageConverter
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

}
