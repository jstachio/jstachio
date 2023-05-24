package io.jstach.opt.spring.webflux.example;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Template;
import io.jstach.jstachio.spi.JStachioExtension;
import io.jstach.opt.spring.SpringJStachio;
import io.jstach.opt.spring.SpringJStachioExtension;

/**
 * Configures JStachio Spring style.
 */
@SuppressWarnings("exports")
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
	public SpringJStachioExtension jstachioService(Environment environment, List<Template<?>> templates) {
		return new SpringJStachioExtension(environment, templates);
	}

	/**
	 * Creates jstachio from found plugins
	 * @param services plugins
	 * @return spring version fo jstachio
	 */
	@Bean
	public SpringJStachio jstachio(List<JStachioExtension> services) {
		var js = new SpringJStachio(services);
		// We need this for the view mixins.
		JStachio.setStatic(() -> js);
		return js;
	}

}
