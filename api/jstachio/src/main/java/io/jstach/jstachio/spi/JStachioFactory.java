package io.jstach.jstachio.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import io.jstach.jstachio.JStachio;

/**
 * Creates JStachios mainly with the {@link ServiceLoader} or a {@link Builder}.
 *
 * @author agentgt
 *
 */
public final class JStachioFactory {

	/**
	 * @hidden
	 */
	private JStachioFactory() {

	}

	/**
	 * Provides a singleton JStachio resolved by the {@link ServiceLoader}
	 * @return service loader based jstachio.
	 */
	public static JStachio defaultJStachio() {
		return Holder.INSTANCE;
	}

	/**
	 * A <em>mutable</em> builder to create {@link JStachio} from
	 * {@link JStachioExtension}s.
	 * @return empty builder
	 */
	public static Builder builder() {
		return new Builder();
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

	/**
	 * Builder for creating jstachios.
	 *
	 * @author agent
	 *
	 */
	public static class Builder {

		private List<JStachioExtension> extensions = new ArrayList<>();

		/**
		 * Adds an extension
		 * @param extension not null
		 * @return this
		 */
		public Builder add(JStachioExtension extension) {
			extensions.add(extension);
			return this;
		}

		/**
		 * Add extensions.
		 *
		 * Useful for adding ServiceLoader results: <pre>
		 * <code class="language-java">
		 * builder.add(ServiceLoader.load(JStachioExtension.class));
		 * </code> </pre>
		 * @param extensions not null
		 * @return this
		 */
		public Builder add(Iterable<JStachioExtension> extensions) {
			extensions.forEach(this.extensions::add);
			return this;
		}

		/**
		 * Builds a JStachio by coalescing the extensions.
		 *
		 * @apiNote See {@link JStachioExtensions} for logic on how the extensions are
		 * consolidated.
		 * @return resolved JStachio
		 */
		public JStachio build() {
			return new DefaultJStachio(JStachioExtensions.of(extensions));
		}

		/**
		 * Current mutable list of extensions.
		 * @return mutable list of extensions
		 */
		public List<JStachioExtension> extensions() {
			return extensions;
		}

	}

}
