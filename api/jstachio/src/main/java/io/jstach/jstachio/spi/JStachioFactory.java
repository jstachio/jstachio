package io.jstach.jstachio.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.TemplateInfo;

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

		private List<TemplateInfo> templates = new ArrayList<>();

		/**
		 * Constructor is hidden for now.
		 */
		private Builder() {
		}

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
		 * Registers an instantiated template. The templates will be added to
		 * {@link JStachioTemplateFinder} with order <code>-1</code> when {@link #build()}
		 * is called.
		 * @param template usually a generated renderer.
		 * @return this
		 */
		public Builder add(TemplateInfo template) {
			Objects.requireNonNull(template, "template");
			templates.add(template);
			return this;
		}

		/**
		 * Registers instantiated templates. The templates will be added
		 * {@link JStachioTemplateFinder} with order <code>-1</code> when {@link #build()}
		 * is called.
		 * @param templates usually a generated renderer.
		 * @return this
		 */
		public Builder add(Collection<? extends TemplateInfo> templates) {
			this.templates.addAll(templates);
			return this;
		}

		/**
		 * Builds a JStachio by coalescing the extensions and registered templates.
		 *
		 * @apiNote See {@link JStachioExtensions} for logic on how the extensions are
		 * consolidated.
		 * @return resolved JStachio
		 */
		public JStachio build() {
			List<JStachioExtension> resolved = new ArrayList<>();
			if (!templates.isEmpty()) {
				var templatesCopy = List.copyOf(templates);
				JStachioTemplateFinder f = JStachioTemplateFinder.of(templatesCopy, -1);
				f = JStachioTemplateFinder.cachedTemplateFinder(f);
				resolved.add(f);
			}
			resolved.addAll(extensions);
			return new DefaultJStachio(JStachioExtensions.of(resolved));
		}

	}

}
