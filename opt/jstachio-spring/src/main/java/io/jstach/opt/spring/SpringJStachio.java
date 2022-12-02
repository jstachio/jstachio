package io.jstach.opt.spring;

import java.util.List;

import io.jstach.jstachio.spi.AbstractJStachio;
import io.jstach.jstachio.spi.JStachioFilter;
import io.jstach.jstachio.spi.JStachioServices;
import io.jstach.jstachio.spi.JStachioServicesContainer;
import io.jstach.jstachio.spi.JStachioTemplateFinder;

public class SpringJStachio extends AbstractJStachio {

	private final JStachioServicesContainer container;

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
