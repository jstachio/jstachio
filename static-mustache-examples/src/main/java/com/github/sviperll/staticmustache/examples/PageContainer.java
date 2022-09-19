package com.github.sviperll.staticmustache.examples;

import com.github.sviperll.staticmustache.GenerateRenderableAdapter;

@GenerateRenderableAdapter(template = "page.mustache")
public class PageContainer {

	private final IdContainer container;

	public PageContainer(
			IdContainer container) {
		super();
		this.container = container;
	}
	
	public IdContainer getContainer() {
		return container;
	}
}
