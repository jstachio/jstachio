package com.github.sviperll.staticmustache.examples;

import com.github.sviperll.staticmustache.GenerateRenderableAdapter;

@GenerateRenderableAdapter(template = "page.mustache")
public class PageContainer {

	private final IdContainer container;
	private final Blog blog;

	public PageContainer(
			IdContainer container, Blog blog) {
		super();
		this.container = container;
		this.blog = blog;
	}
	
	public IdContainer getContainer() {
		return container;
	}
	
	public Blog getBlog() {
		return blog;
	}
	
}
