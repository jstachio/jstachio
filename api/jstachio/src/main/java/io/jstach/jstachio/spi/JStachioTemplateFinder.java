package io.jstach.jstachio.spi;

import io.jstach.jstache.JStache;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateInfo;

/**
 * Finds templates based on the model type (class).
 * <p>
 * The default {@link JStachio} uses a combination of relection and the ServiceLoader to
 * find templates.
 * <p>
 * Other implementations may want to use their DI framework like Spring or CDI to find
 * templates.
 *
 * @author agentgt
 *
 */
public interface JStachioTemplateFinder {

	/**
	 * Finds a {@link Template} if possible otherwise possibly falling back to a
	 * {@link TemplateInfo} based on annotation metadata or some other mechanism.
	 *
	 * @apiNote Callers can do an <code>instanceof Template t</code> to see if a generated
	 * template was returned instead of the fallback {@link TemplateInfo} metadata.
	 * @param modelType the models class (<em>the one annotated with {@link JStache} and
	 * not the Templates class</em>)
	 * @return the template info which might be a {@link Template} if the generated
	 * template was found.
	 * @throws Exception if any reflection error happes or the template is not found
	 */
	public TemplateInfo findTemplate(Class<?> modelType) throws Exception;

}

class DefaultTemplateFinder implements JStachioTemplateFinder {

	private final JStachioConfig config;

	public DefaultTemplateFinder(JStachioConfig config) {
		super();
		this.config = config;
	}

	@Override
	public TemplateInfo findTemplate(Class<?> modelType) throws Exception {
		return Templates.findTemplate(modelType, config);
	}

}
