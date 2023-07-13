package io.jstach.opt.dropwizard;

import io.dropwizard.views.common.View;

/**
 * Have JStache models implement this mixin interface for easier support to generate views
 * from models.
 * <p>
 * To enforce all models implement this interface configurure JStachio like:
 * <pre><code class="language-java">
 * &#64;JStacheConfig(interfacing = &#64;JStacheInterfaces(modelImplements = JStacheViewSupport.class))
 * &#47;&#47; some class, package-info, module-info
 * </code> </pre>
 * <p>
 * If using this mixin interface is not desirable one can create view manually from a
 * model with {@link JStachioView#of(Object)}.
 *
 * @author agentgt
 */
public interface JStacheViewSupport {

	/**
	 * Creates Dropwizard view from this model
	 * @return dropwizard view
	 */
	@SuppressWarnings("exports")
	default View toView() {
		return JStachioView.of(this);
	}

}
