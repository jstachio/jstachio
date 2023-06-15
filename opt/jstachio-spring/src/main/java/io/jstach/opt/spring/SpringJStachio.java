package io.jstach.opt.spring;

import java.util.List;

import io.jstach.jstachio.spi.AbstractJStachio;
import io.jstach.jstachio.spi.JStachioExtension;
import io.jstach.jstachio.spi.JStachioExtensions;

/**
 * A JStachio that does not use the service loader.
 *
 * @author agentgt
 */
public class SpringJStachio extends AbstractJStachio {

	private final JStachioExtensions extensions;

	/**
	 * Passed the found services usually injected by Spring.
	 * @param extensions not null.
	 */
	public SpringJStachio(List<JStachioExtension> extensions) {
		this(JStachioExtensions.of(extensions));
	}

	/**
	 * Passed the found services usually injected by Spring.
	 * @param extensions not null.
	 */
	public SpringJStachio(JStachioExtensions extensions) {
		this.extensions = extensions;
	}

	@Override
	public JStachioExtensions extensions() {
		return this.extensions;
	}

}
