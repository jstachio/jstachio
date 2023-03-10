package io.jstach.jstachio.spi;

import java.util.ServiceLoader;

/**
 * An SPI extension point via the {@link ServiceLoader} or other mechanisms like DI.
 *
 * If the extension needs configuration for initialization see
 * {@link JStachioExtensionProvider}.
 *
 * @apiNote Because this is a sealed class one must implement the permitted classes which
 * are non-sealed.
 * @author agentgt
 * @see JStachioExtensions
 */
public sealed interface JStachioExtension permits JStachioExtensionProvider, JStachioFilter, JStachioTemplateFinder, JStachioConfig {

}
