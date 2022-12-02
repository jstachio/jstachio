package io.jstach.opt.spring.example;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.jstach.jstachio.JStachio;
import io.jstach.opt.spring.JStachioHttpMessageConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new JStachioHttpMessageConverter(JStachio.of()));
	}

}
