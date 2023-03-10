package io.jstach.jstachio.spi;

import org.eclipse.jdt.annotation.Nullable;

/**
 * An extension that is a factory that provides other extensions.
 *
 * All methods are optional (default) so that implementations can decide what particularly
 * plugins/services they want to provide.
 *
 * This is extension is useful if there is configuration logic that needs to happen before
 * creation. See {@link #init(JStachioConfig)} and {@link #provideConfig()}.
 *
 * @author agentgt
 * @see JStachioExtensions
 * @see JStachioExtension
 */
public non-sealed interface JStachioExtensionProvider extends JStachioExtension {

	/**
	 * Provide a filter or not. The final filter is a composite and becomes a filter
	 * chain.
	 * @return filter if this service provider provies one or <code>null</code>
	 */
	default @Nullable JStachioFilter provideFilter() {
		return null;
	}

	/**
	 * Provide a config or not. The final config is a composite of all the found configs.
	 * <p>
	 * Specifically if multiple instances of {@link JStachioExtension} are found that
	 * return a nonnull they will be combined by looping through all of them to find a
	 * nonnull value for {@link JStachioConfig#getProperty(String)}. <strong>If no configs
	 * are provided or no services found {@link JStachioExtensions} instance will use the
	 * default config which uses {@link System#getProperties()}</strong>.
	 *
	 * @apiNote This method is called before {@link #init(JStachioConfig)}
	 * @return config if this service provides one or <code>null</code>
	 */
	default @Nullable JStachioConfig provideConfig() {
		return null;
	}

	/**
	 * Provide a template finder or not. The final template finder is a composite of all
	 * the other ones found. See {@link JStachioTemplateFinder#order()} for ordering
	 * details.
	 * <p>
	 * <strong>If no template finders are provided then the default template finder that
	 * uses reflection and the ServiceLoader is used.</strong>
	 * @return template finder or not
	 */
	default @Nullable JStachioTemplateFinder provideTemplateFinder() {
		return null;
	}

	/**
	 * Called before the extensions are used but after {@link #provideConfig()}. See
	 * {@link #provideConfig()} on how the config is consolidated to a single config.
	 * @param config the composite config never null
	 */
	default void init(JStachioConfig config) {
	}

	/**
	 * Creates a provider from an extension. If the extension is a provider than it is
	 * returned without wrapping.
	 *
	 * If the extension represent multiple types of extensions the returned provider will
	 * provide all of them.
	 * @param extension the extension to be wrapped.
	 * @return provider that will provide the extension
	 */
	public static JStachioExtensionProvider of(JStachioExtension extension) {
		if (extension instanceof JStachioExtensionProvider p) {
			return p;
		}
		JStachioFilter filter = null;
		JStachioConfig config = null;
		JStachioTemplateFinder finder = null;
		if (extension instanceof JStachioFilter _filter) {
			filter = _filter;
		}
		if (extension instanceof JStachioConfig _config) {
			config = _config;
		}
		if (extension instanceof JStachioTemplateFinder _finder) {
			finder = _finder;
		}
		return new ExtensionProvider(config, filter, finder);
	}

}

record ExtensionProvider(@Nullable JStachioConfig config, @Nullable JStachioFilter filter,
		@Nullable JStachioTemplateFinder finder) implements JStachioExtensionProvider {
}