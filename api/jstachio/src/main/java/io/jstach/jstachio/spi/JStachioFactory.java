package io.jstach.jstachio.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

import io.jstach.jstache.JStacheCatalog;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.TemplateInfo;

/**
 * Creates JStachios mainly with the {@link ServiceLoader} or a {@link Builder}.
 *
 * @author agentgt
 * @see JStacheCatalog
 * @see JStachioExtensions
 */
public final class JStachioFactory {

	/**
	 * @hidden
	 */
	private JStachioFactory() {

	}

	/**
	 * Provides a singleton JStachio resolved by the {@link ServiceLoader}.
	 * <p>
	 * Because of differences to how the {@link ServiceLoader} works with modular
	 * applications registration of generated templates is different. For modular
	 * applications you can either allow reflective access to JStachio:
	 *
	 * <pre><code class="language-java">
	 * &#47;&#47; module-info.java
	 * opens packagewith.jstachemodels to io.jstach.jstachio;
	 * </code> </pre>
	 *
	 * Or you can generate a catalog of all templates and register them. See
	 * {@link JStacheCatalog} for details.
	 * @return service loader based jstachio.
	 */
	public static JStachio defaultJStachio() {
		return Holder.INSTANCE;
	}

	/**
	 * A <em>mutable</em> builder to create {@link JStachio} from
	 * {@link JStachioExtension}s. Once {@link Builder#build()} is called the returned
	 * JStachio will be immutable. If no extensions are added the returned JStachio will
	 * be resolved in a simlar manner to the {@link #defaultJStachio() default JStachio}.
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
			return new Holder(JStachioExtensions.of());
		}

		@Override
		public JStachioExtensions extensions() {
			return this.extensions;
		}

		@Override
		public String toString() {
			return "ServiceLoaderJStachio";
		}

	}

	/**
	 * Builder for creating a custom JStachio.
	 *
	 * <pre><code class="language-java">
	 * JStachio jstachio = JStachioFactory.builder()
	 *     .add(extension1)
	 *     .add(extension2)
	 *     .build();
	 * </code></pre>
	 *
	 * <em> The order of adding extensions is important such that primacy order takes
	 * precedence as composite extensions such as config will be created if multiple of
	 * the same extension type are added. </em> If you would like to share the JStachio in
	 * a service locator style you may want to set it as the default via
	 * {@link JStachio#setStatic(java.util.function.Supplier)} which will make all calls
	 * of {@link JStachio#of()} use the custom one.
	 *
	 * @author agentgt
	 * @see JStacheCatalog
	 * @see JStachioTemplateFinder
	 * @see JStachioConfig
	 * @see JStachio#setStatic(java.util.function.Supplier)
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
		public Builder add(Iterable<? extends JStachioExtension> extensions) {
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
