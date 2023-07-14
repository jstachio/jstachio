package io.jstach.opt.dropwizard;

import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheInterfaces;
import io.jstach.jstache.JStachePath;

/**
 * Opinionated static config for dropwizard based on how dropwizard stores templates for
 * Mustache.java. Place the below on a <code>package-info.java</code> where your JStache
 * models are or if in a modular environment <code>module-info.java</code>
 *
 * <pre><code class="language-java">
 * &#64;JStacheConfig(using = DropwizardJStacheConfig.class)
 * &#47;&#47; some class, package-info, module-info
 * </code>
 *
 * </pre> Assuming you have a JStache model with class name <code>MyView</code> in package
 * <code>com.company.model</code> The template location if not specified will default to:
 * <code>src/main/resources/com/company/model/MyModel.mustache</code>.
 * <p>
 * This configuration will also enforce and check that your models <code>implements</code>
 * {@link JStacheViewSupport} which provides {@link JStacheViewSupport#toView()}.
 * @author agentgt
 *
 */
@JStacheConfig(pathing = @JStachePath(suffix = ".mustache"),
		interfacing = @JStacheInterfaces(modelImplements = JStacheViewSupport.class,
				templateImplements = ViewableTemplate.class))
public enum DropwizardJStacheConfig {

	// purposely empty

}
