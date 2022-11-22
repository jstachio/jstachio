package io.jstach.jstachio.spi;

import java.util.List;

import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateConfig;

/**
 * A {@link java.util.ServiceLoader} interface for finding {@link Template}s.
 * <p>
 * In non modular applications the Templates can be found using this interface and the
 * {@link java.util.ServiceLoader} mechanism through the <code>META-INF/services</code>
 * file as the code generator generates the services file. However in modular applications
 * this is not possible as the implementations are described in the module-info.java and
 * the code generator does not touch that.
 * <p>
 * Regardless of modular or not the generated META-INF/services also might give hints to
 * Graal native compilation for reflective access to the templates.
 *
 * @author agentgt
 */
public interface TemplateProvider {

	/**
	 * Provides a list of instantiated renderers.
	 * @param templateConfig template collaborators.
	 * @return a list of renderers. An empty list would mean none were found.
	 */
	public List<Template<?>> provideTemplates(TemplateConfig templateConfig);

	/**
	 * Provides templates with empty config.
	 * @return a list of templates. An empty list would mean none were fond.
	 */
	default List<Template<?>> provideTemplates() {
		return provideTemplates(TemplateConfig.empty());
	}

}
