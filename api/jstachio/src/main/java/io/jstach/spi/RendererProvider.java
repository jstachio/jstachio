package io.jstach.spi;

import java.util.List;

import io.jstach.Renderer;

public interface RendererProvider {

	public List<Renderer<?>> provideRenderers();

}
