package com.github.sviperll.staticmustache.examples;

import java.util.List;

import com.github.sviperll.staticmustache.GenerateRenderableAdapter;

@GenerateRenderableAdapter(template = "page.mustache")
public class PageContainer {

	private final IdContainer container;
	private final List<Post> posts;

	public PageContainer(
			IdContainer container, List<Post> posts) {
		super();
		this.container = container;
		this.posts = posts;
	}
	
	public IdContainer getContainer() {
		return container;
	}
	
	public List<Post> getPosts() {
		return posts;
	}
	
}
