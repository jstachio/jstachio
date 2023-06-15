package io.jstach.opt.spring;

import java.util.List;
import java.util.ServiceLoader;

import org.eclipse.jdt.annotation.Nullable;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;

import io.jstach.jstachio.Template;
import io.jstach.jstachio.spi.JStachioConfig;
import io.jstach.jstachio.spi.JStachioExtensionProvider;
import io.jstach.jstachio.spi.JStachioTemplateFinder;

/**
 * JStachio services based on Spring notably the {@linkplain #provideConfig() config} and
 * {@linkplain #provideTemplateFinder() template finding}.
 *
 * @apiNote Although this is an extension it is not designed to be loaded by the
 * {@link ServiceLoader}.
 * @author agentgt
 *
 */
public class SpringJStachioExtension implements JStachioExtensionProvider {

	private final JStachioConfig config;

	private final JStachioTemplateFinder templateFinder;

	/**
	 * Constructor for injection
	 * @param config jstachio config see {@link #config(PropertyResolver)}
	 * @param templateFinder template finder to use
	 */
	public SpringJStachioExtension(JStachioConfig config, JStachioTemplateFinder templateFinder) {
		super();
		this.config = config;
		this.templateFinder = templateFinder;
	}

	/**
	 * Creates a JStachio config from a property resolver (usually {@link Environment}).
	 * @param propertyResolver wrapper property resolver
	 * @return config
	 */
	public static JStachioConfig config(@SuppressWarnings("exports") PropertyResolver propertyResolver) {
		return new SpringJStachioConfig(propertyResolver);
	}

	/**
	 * Constructor for injection
	 * @param environment springs environment to be used for {@link JStachioConfig}
	 * @param templates templates found via spring
	 */
	public SpringJStachioExtension(@SuppressWarnings("exports") Environment environment, List<Template<?>> templates) {
		this(config(environment),
				JStachioTemplateFinder.cachedTemplateFinder(JStachioTemplateFinder.of(templates, -1)));
	}

	/**
	 * {@inheritDoc} The config comes from Spring {@link Environment} abstraction.
	 */
	@Override
	public JStachioConfig provideConfig() {
		return this.config;
	}

	/**
	 * {@inheritDoc} The provided template finder instead of using reflection delegates to
	 * the templates wired in via spring.
	 */
	@Override
	public JStachioTemplateFinder provideTemplateFinder() {
		return this.templateFinder;
	}

	private static final class SpringJStachioConfig implements JStachioConfig {

		private final PropertyResolver propertyResolver;

		SpringJStachioConfig(PropertyResolver propertyResolver) {
			super();
			this.propertyResolver = propertyResolver;
		}

		@Override
		public @Nullable String getProperty(String key) {
			return propertyResolver.getProperty(key);
		}

	}

}
