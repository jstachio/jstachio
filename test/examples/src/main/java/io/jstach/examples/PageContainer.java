package io.jstach.examples;

import io.jstach.jstache.JStache;

@JStache(path = "page.mustache")
class PageContainer implements Mixin {

	private final IdContainer container;

	private final Blog blog;

	public PageContainer(IdContainer container, Blog blog) {
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
