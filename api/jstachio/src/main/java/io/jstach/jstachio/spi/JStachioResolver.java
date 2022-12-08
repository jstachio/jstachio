package io.jstach.jstachio.spi;

import java.util.ServiceLoader;

import io.jstach.jstachio.JStachio;

/**
 * Finds JStachios mainly with the {@link ServiceLoader}.
 *
 * @author agentgt
 *
 */
public final class JStachioResolver {

	/**
	 * @hidden
	 */
	private JStachioResolver() {

	}

	/**
	 * Provides a singleton JStachio resolved by the {@link ServiceLoader}
	 * @return service loader based jstachio.
	 */
	public static JStachio defaultJStachio() {
		return Holder.INSTANCE;
	}

	private static class Holder extends AbstractJStachio {

		private static Holder INSTANCE = Holder.of();

		private final JStachioExtensions extensions;

		public Holder(JStachioExtensions extensions) {
			this.extensions = extensions;
		}

		private static Holder of() {
			Iterable<JStachioExtension> it = ServiceLoader.load(JStachioExtension.class);
			return new Holder(JStachioExtensions.of(it));
		}

		@Override
		public JStachioExtensions extensions() {
			return this.extensions;
		}

	}

}
