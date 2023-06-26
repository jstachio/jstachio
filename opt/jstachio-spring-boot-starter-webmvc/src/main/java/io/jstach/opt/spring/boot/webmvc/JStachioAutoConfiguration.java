package io.jstach.opt.spring.boot.webmvc;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import io.jstach.jstachio.JStachio;

/**
 * Auto Configures JStachio for Spring Boot.
 *
 * Templates are loaded from the ServiceLoader and are then registered in the
 * ApplicationContext. Extensions that are wired by Spring will also be discovered as well
 * as ServiceLoader based extensions that are not already wired as beans.
 *
 * @author agentgt
 * @author dsyer
 * @apiNote while this class and methods on this class are public for Spring reflection it
 * is not intended to be true public API.
 */
@AutoConfiguration(before = { ServletWebServerFactoryAutoConfiguration.class })
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass(JStachio.class)
@Import(JStachioConfiguration.class)
public class JStachioAutoConfiguration {

	/**
	 * Default constructor called by Spring
	 */
	public JStachioAutoConfiguration() {
	}

}
