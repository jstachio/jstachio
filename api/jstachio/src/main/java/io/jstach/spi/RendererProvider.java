package io.jstach.spi;

import java.util.List;

import io.jstach.Renderer;

/**
 * A {@link java.util.ServiceLoader} interface for finding {@link Renderer}s.
 * <p>
 * In non modular applications the Renderers can be found using this interface and the
 * {@link java.util.ServiceLoader} mechanism through the <code>META-INF/services</code>
 * file. However in modular applications this is not possible as the implementations are
 * described in the module-info.java and the code generator does not touch that.
 *
 * @author agentgt
 */
public interface RendererProvider {

	/**
	 * Provides a list of instantiated renderers.
	 * @return a list of renderers. An empty list would mean none were found.
	 */
	public List<Renderer<?>> provideRenderers();

}
