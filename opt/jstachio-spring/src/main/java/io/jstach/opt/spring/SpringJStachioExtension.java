package io.jstach.opt.spring;

import java.util.List;
import java.util.ServiceLoader;

import org.springframework.core.env.Environment;

import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateInfo;
import io.jstach.jstachio.spi.JStachioConfig;
import io.jstach.jstachio.spi.JStachioExtension;
import io.jstach.jstachio.spi.JStachioTemplateFinder;

/**
 * JStachio services based on Spring notably the {@link #provideConfig() config} and
 * {@link #provideTemplateFinder() template finding}.
 *
 * @apiNote Although this is an extension it is not designed to be loaded by the
 * {@link ServiceLoader}.
 * @author agentgt
 *
 */
public class SpringJStachioExtension implements JStachioExtension {

	private final Environment environment;

	private final List<Template<?>> templates;

	/**
	 * Constructor for injection
	 * @param environment springs environment to be used for {@link JStachioConfig}
	 * @param templates templates found via spring
	 */
	public SpringJStachioExtension(@SuppressWarnings("exports") Environment environment, List<Template<?>> templates) {
		super();
		this.environment = environment;
		this.templates = templates;
	}

	/**
	 * {@inheritDoc} The config comes from Spring {@link Environment} abstraction.
	 */
	@Override
	public JStachioConfig provideConfig() {
		return prop -> environment.getProperty(prop);
	}

	/**
	 * {@inheritDoc} The provided template finder instead of using reflection delegates to
	 * the templates wired in via spring.
	 */
	@Override
	public JStachioTemplateFinder provideTemplateFinder() {

		return new JStachioTemplateFinder() {

			@Override
			public TemplateInfo findTemplate(Class<?> modelType) throws Exception {
				/*
				 * TODO should we do reflection based lookup if this fails?
				 */
				for (var t : templates) {
					if (t.supportsType(modelType)) {
						return t;
					}
				}
				throw new RuntimeException("template not found for type: " + modelType);
			}

			@Override
			public boolean supportsType(Class<?> modelType) {
				for (var t : templates) {
					if (t.supportsType(modelType)) {
						return true;
					}
				}
				return false;
			}
		};

	}

}
