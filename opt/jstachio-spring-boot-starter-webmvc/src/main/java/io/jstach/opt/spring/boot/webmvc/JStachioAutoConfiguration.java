package io.jstach.opt.spring.boot.webmvc;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import io.jstach.jstachio.JStachio;

/**
 * Auto Configures JStachio for Spring Boot
 *
 * @author agentgt
 *
 */
@AutoConfiguration(before = { ServletWebServerFactoryAutoConfiguration.class })
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass(JStachio.class)
@Import(SpringTemplateConfig.class)
public class JStachioAutoConfiguration {

	/**
	 * Default constructor called by Spring
	 */
	public JStachioAutoConfiguration() {
	}

}
