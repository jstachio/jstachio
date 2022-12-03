package io.jstach.opt.spring;

import java.util.List;

import io.jstach.jstachio.spi.AbstractJStachio;
import io.jstach.jstachio.spi.JStachioFilter;
import io.jstach.jstachio.spi.JStachioServices;
import io.jstach.jstachio.spi.JStachioServicesContainer;
import io.jstach.jstachio.spi.JStachioTemplateFinder;

/**
 * A JStachio that does not use the service loader.
 *
 * @author agentgt
 */
public class SpringJStachio extends AbstractJStachio {

	private final JStachioServicesContainer container;

	/**
	 * Passed the found services usually injected by Spring.
	 * @param services not null.
	 */
	public SpringJStachio(List<JStachioServices> services) {
		container = JStachioServicesContainer.of(services);
	}

	@Override
	protected JStachioTemplateFinder templateFinder() {
		return container.getTemplateFinder();
	}

	@Override
	protected JStachioFilter filter() {
		return container.getFilter();
	}

}
