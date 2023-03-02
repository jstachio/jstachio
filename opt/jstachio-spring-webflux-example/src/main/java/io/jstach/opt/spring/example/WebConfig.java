package io.jstach.opt.spring.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import io.jstach.jstachio.JStachio;
import io.jstach.opt.spring.web.JStachioHttpMessageConverter;
import io.jstach.opt.spring.webflux.JStachioEncoder;
import io.jstach.opt.spring.webflux.ViewSetupBeanPostProcessor;

/**
 * Configures MVC using {@link JStachioHttpMessageConverter} to allow returning models
 * which will be rendered using JStachio runtime.
 *
 * @author agentgt
 * @see JStachioHttpMessageConverter
 */
@Configuration
public class WebConfig implements WebFluxConfigurer {

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
	public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
		configurer.customCodecs().register(new JStachioEncoder(jstachio));
	}

	@Bean
	@SuppressWarnings("exports")
	public ViewSetupBeanPostProcessor viewSetupHandlerInterceptor(ApplicationContext context) {
		return new ViewSetupBeanPostProcessor(context);
	}

}
