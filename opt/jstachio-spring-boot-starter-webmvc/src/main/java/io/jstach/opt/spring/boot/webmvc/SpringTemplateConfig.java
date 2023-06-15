package io.jstach.opt.spring.boot.webmvc;

import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Template;
import io.jstach.jstachio.spi.JStachioExtension;
import io.jstach.jstachio.spi.TemplateProvider;
import io.jstach.jstachio.spi.Templates;
import io.jstach.opt.spring.SpringJStachio;
import io.jstach.opt.spring.SpringJStachioExtension;

/**
 * Configures JStachio Spring style.
 */
@Configuration
public class SpringTemplateConfig {

	private static final Log logger = LogFactory.getLog(SpringTemplateConfig.class);

	private final ConfigurableListableBeanFactory beanFactory;

	/**
	 * Do nothing constructor to placate jdk 18 javadoc
	 * @param beanFactory used to register the serviceloader found templates
	 */
	public SpringTemplateConfig(@SuppressWarnings("exports") ConfigurableListableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Templates found with the service loader
	 * @return templates
	 */
	@Bean
	public List<Template<?>> templatesByServiceLoader() {
		var serviceLoader = ServiceLoader.load(TemplateProvider.class);
		var templates = Templates.findTemplates(serviceLoader, e -> {
			logger.error("Failed to load template provider. Skipping it.", e);
		}).toList();
		for (var t : templates) {
			this.beanFactory.registerSingleton(t.getClass().getName(), t);
		}
		return templates;
	}

	/**
	 * Creates a services based on spring objects.
	 * @param environment used for config
	 * @return the services
	 */
	@Bean
	@SuppressWarnings("exports")
	public SpringJStachioExtension springJStachioExtension(Environment environment) {
		return new SpringJStachioExtension(environment, templatesByServiceLoader());
	}

	/**
	 * Creates jstachio from found plugins
	 * @param services plugins
	 * @return spring version fo jstachio
	 */
	@Bean
	public SpringJStachio jstachio(List<JStachioExtension> services) {
		for (var s : services) {
			logger.info("JStachio will load extension: " + s.getClass());
		}
		var js = new SpringJStachio(services);
		// We need this for the view mixins.
		JStachio.setStatic(() -> js);
		return js;
	}

	/**
	 * Extensions found with the service loader
	 * @return extensions
	 */
	@Bean
	public List<JStachioExtension> extensionsByServiceLoader() {
		/*
		 * In a modular world the service loader is the better solution to find extensions
		 * then using Spring Boots ConditionalOnClass
		 */
		var extensions = ServiceLoader.load(JStachioExtension.class).stream().map(p -> p.get()).toList();
		return extensions;

	}

}
