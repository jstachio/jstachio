package io.jstach.opt.spring.example;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.jstach.jstachio.JStachio;
import io.jstach.opt.spring.JStachioHttpMessageConverter;

/**
 * @hidden
 * @author agentgt
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final JStachio jstachio;

	@Autowired
	public WebConfig(JStachio jstachio) {
		this.jstachio = jstachio;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new JStachioHttpMessageConverter(jstachio));
	}

}
