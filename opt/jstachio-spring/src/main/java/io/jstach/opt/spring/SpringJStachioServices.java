package io.jstach.opt.spring;

import java.util.List;

import org.springframework.core.env.Environment;

import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateInfo;
import io.jstach.jstachio.spi.JStachioConfig;
import io.jstach.jstachio.spi.JStachioServices;
import io.jstach.jstachio.spi.JStachioTemplateFinder;

/**
 * JStachio services based on Spring
 *
 * @author agent
 *
 */
public class SpringJStachioServices implements JStachioServices {

	private final Environment environment;

	private final List<Template<?>> templates;

	/**
	 * Constructor for injection
	 * @param environment springs environment to be used for {@link JStachioConfig}
	 * @param templates templates found via spring
	 */
	public SpringJStachioServices(Environment environment, List<Template<?>> templates) {
		super();
		this.environment = environment;
		this.templates = templates;
	}

	@Override
	public JStachioConfig provideConfig() {
		return prop -> environment.getProperty(prop);
	}

	@Override
	public JStachioTemplateFinder provideTemplateFinder() {

		return new JStachioTemplateFinder() {

			@Override
			public TemplateInfo findTemplate(Class<?> modelType) throws Exception {
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
