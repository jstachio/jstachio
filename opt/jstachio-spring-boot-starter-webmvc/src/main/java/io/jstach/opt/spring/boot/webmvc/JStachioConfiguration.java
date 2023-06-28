package io.jstach.opt.spring.boot.webmvc;

import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateConfig;
import io.jstach.jstachio.spi.JStachioConfig;
import io.jstach.jstachio.spi.JStachioExtension;
import io.jstach.jstachio.spi.JStachioTemplateFinder;
import io.jstach.jstachio.spi.TemplateProvider;
import io.jstach.jstachio.spi.Templates;
import io.jstach.opt.spring.SpringJStachio;
import io.jstach.opt.spring.SpringJStachioExtension;

/**
 * Configures JStachio Spring style.
 * <p>
 * Templates are loaded from the ServiceLoader and are then registered in the
 * ApplicationContext. Extensions that are wired by Spring will also be discovered as well
 * as ServiceLoader based extensions that are not already wired as beans.
 *
 * @author agentgt
 * @author dsyer
 * @apiNote while this class and methods on this class are public for Spring reflection it
 * is not intended to be true public API.
 */
@Configuration
public class JStachioConfiguration {

	private static final Log logger = LogFactory.getLog(JStachioConfiguration.class);

	private final ConfigurableListableBeanFactory beanFactory;

	/**
	 * Do nothing constructor to placate jdk 18 javadoc
	 * @param beanFactory used to register the serviceloader found templates
	 */
	public JStachioConfiguration(@SuppressWarnings("exports") ConfigurableListableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Templates found with the service loader
	 * @param templateConfig used to create singleton templates
	 * @return templates
	 * @see #templateConfig()
	 */
	@Bean
	public List<Template<?>> templatesByServiceLoader(TemplateConfig templateConfig) {
		var serviceLoader = serviceLoader(TemplateProvider.class);
		var templates = Templates.findTemplates(serviceLoader, templateConfig, e -> {
			logger.error("Failed to load template provider. Skipping it.", e);
		}).toList();
		for (var t : templates) {
			this.beanFactory.registerSingleton(t.getClass().getName(), t);
		}
		return templates;
	}

	/**
	 * Resolve config from spring environment
	 * @param environment for properties
	 * @return config
	 */
	@Bean
	@ConditionalOnMissingBean(JStachioConfig.class)
	public JStachioConfig config(@SuppressWarnings("exports") Environment environment) {
		return SpringJStachioExtension.config(environment);
	}

	/**
	 * Resolve template finder configs
	 * @param config jstachio config
	 * @param templateConfig the template config
	 * @return spring powered template finder
	 */
	@Bean
	@ConditionalOnMissingBean(JStachioTemplateFinder.class)
	public JStachioTemplateFinder templateFinder(JStachioConfig config, TemplateConfig templateConfig) {
		var templates = templatesByServiceLoader(templateConfig);
		var springTemplateFinder = JStachioTemplateFinder.cachedTemplateFinder(JStachioTemplateFinder.of(templates, 0));
		var fallbackFinder = JStachioTemplateFinder.defaultTemplateFinder(config);
		return JStachioTemplateFinder.of(List.of(springTemplateFinder, fallbackFinder));
	}

	/**
	 * The default template config is empty and will let each template resolve its own
	 * config. The template config contains an optional formatter (nullable) and optional
	 * escaper (nullable). If a template config is provided as a bean somewhere else it
	 * will replace this default. The only time this could be of use is if you needed a
	 * formatter or escaper with custom wiring.
	 * @return empty template config.
	 * @see TemplateConfig#empty()
	 */
	@Bean
	@ConditionalOnMissingBean(TemplateConfig.class)
	public TemplateConfig templateConfig() {
		return TemplateConfig.empty();
	}

	/**
	 * Creates a services based on spring objects.
	 * @param config used for config
	 * @param templateFinder used to find templates
	 * @return spring powered jstatchio extension provider
	 * @see TemplateConfig#empty()
	 */
	@Bean
	public SpringJStachioExtension springJStachioExtension(JStachioConfig config,
			JStachioTemplateFinder templateFinder) {
		return new SpringJStachioExtension(config, templateFinder);
	}

	/**
	 * Creates jstachio from found plugins
	 * @param extensions plugins
	 * @return spring version fo jstachio
	 */
	@Bean
	public SpringJStachio jstachio(List<JStachioExtension> extensions) {
		Set<Class<?>> extensionClasses = extensions.stream().map(e -> e.getClass())
				.collect(Collectors.toCollection(HashSet::new));
		/*
		 * We attempt to filter already loaded extensions via the service loader.
		 *
		 * We should probably make this configurable.
		 */
		List<JStachioExtension> serviceLoaderExtensions = serviceLoader(JStachioExtension.class) //
				.stream() //
				.filter(p -> !extensionClasses.contains(p.type())) //
				.map(p -> p.get()) //
				.toList();

		for (var s : serviceLoaderExtensions) {
			logger.info("JStachio found extension by ServiceLoader: " + s.getClass());
		}

		extensions = Stream.concat(extensions.stream(), serviceLoaderExtensions.stream()).toList();

		var js = new SpringJStachio(extensions);
		// We need this for the view mixins.
		JStachio.setStatic(() -> js);
		return js;
	}

	private <T> ServiceLoader<T> serviceLoader(Class<T> spiClass) {
		ClassLoader classLoader = beanFactory.getBeanClassLoader();
		return classLoader == null ? ServiceLoader.load(spiClass) : ServiceLoader.load(spiClass, classLoader);
	}

}
