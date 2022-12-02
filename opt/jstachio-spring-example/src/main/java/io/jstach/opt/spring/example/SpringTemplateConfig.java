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

@Configuration
public class SpringTemplateConfig {

	@Bean
	public SpringJStachioServices jstachioService(Environment environment, List<Template<?>> templates) {
		return new SpringJStachioServices(environment, templates);
	}

	@Bean
	public SpringJStachio jstachio(List<JStachioServices> services) {
		var js = new SpringJStachio(services);
		JStachio.setStaticJStachio(() -> js);
		return js;
	}

	@Bean
	public JStachioServices jmustache() {
		return new JMustacheRenderer();
	}

}
