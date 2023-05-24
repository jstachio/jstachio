package io.jstach.opt.spring.example.message;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jstach.opt.spring.webmvc.JStachioModelViewConfigurer;

/**
 * Message wiring.
 * @author agent
 *
 */
@Configuration
public class MessageConfiguration {

	/**
	 * Bean to create configurer bean that does cross cutting logic
	 * across controllers
	 * @return configurer
	 */
	@Bean
	public JStachioModelViewConfigurer configurer() {
		return (page, model, request) -> {
			if (page instanceof MessagePage message) {
				message.message = "Hello configured!";
			}
		};
	}

}
