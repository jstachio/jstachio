package io.jstach.opt.spring.webflux.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import io.jstach.jstachio.JStachio;
import io.jstach.opt.spring.web.JStachioHttpMessageConverter;
import io.jstach.opt.spring.webflux.JStachioEncoder;
import io.jstach.opt.spring.webflux.JStachioModelViewConfigurer;
import io.jstach.opt.spring.webflux.ViewSetupBeanPostProcessor;

/**
 * Configures MVC using {@link JStachioHttpMessageConverter} to allow returning models
 * which will be rendered using JStachio runtime.
 *
 * @author agentgt
 * @see JStachioHttpMessageConverter
 */
@SuppressWarnings("exports")
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

	/**
	 * Factory to create a bean post processor to register
	 * {@link JStachioModelViewConfigurer}s.
	 * @param context supplied by spring
	 * @return a post processor that will configure JStachio Model Views before being
	 * rendered.
	 */
	@Bean
	public ViewSetupBeanPostProcessor viewSetupHandlerInterceptor(ApplicationContext context) {
		return new ViewSetupBeanPostProcessor(context);
	}

}
