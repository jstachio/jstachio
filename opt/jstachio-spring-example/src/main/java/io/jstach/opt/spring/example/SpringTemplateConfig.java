package io.jstach.opt.spring.example;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Template;
import io.jstach.jstachio.spi.JStachioServices;
import io.jstach.opt.jmustache.JMustacheRenderer;
import io.jstach.opt.spring.SpringJStachio;
import io.jstach.opt.spring.SpringJStachioServices;

/**
 * Configures JStachio Spring style.
 */
@Configuration
public class SpringTemplateConfig {

	/**
	 * Do nothing constructor to placate jdk 18 javadoc
	 */
	public SpringTemplateConfig() {
	}

	/**
	 * Creates a services based on spring objects.
	 * @param environment used for config
	 * @param templates found templates via component scanning
	 * @return the services
	 */
	@Bean
	public SpringJStachioServices jstachioService(Environment environment, List<Template<?>> templates) {
		return new SpringJStachioServices(environment, templates);
	}

	/**
	 * Creates jstachio from found plugins
	 * @param services plugins
	 * @return spring version fo jstachio
	 */
	@Bean
	public SpringJStachio jstachio(List<JStachioServices> services) {
		var js = new SpringJStachio(services);
		JStachio.setStaticJStachio(() -> js);
		return js;
	}

	/**
	 * The JMustache plugin to render templates while editing in development mode.
	 * @return jmustache plugin
	 */
	@Bean
	public JStachioServices jmustache() {
		return new JMustacheRenderer();
	}

}
