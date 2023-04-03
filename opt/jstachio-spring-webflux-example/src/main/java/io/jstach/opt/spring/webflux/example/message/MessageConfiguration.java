package io.jstach.opt.spring.webflux.example.message;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jstach.opt.spring.webflux.JStachioModelViewConfigurer;

/**
 *
 * Spring configuration to add a {@link JStachioModelViewConfigurer} for MessagePage.
 *
 * @author dsyer
 *
 */
@Configuration
public class MessageConfiguration {

	/**
	 * Called by Spring
	 */
	public MessageConfiguration() {
	}

	/**
	 * Creates the configurer for Spring
	 * @return configuer specific to Message
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
