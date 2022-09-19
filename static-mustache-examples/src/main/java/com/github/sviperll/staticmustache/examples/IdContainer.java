package com.github.sviperll.staticmustache.examples;

import java.util.UUID;

import com.github.sviperll.staticmustache.GenerateRenderableAdapter;

public class IdContainer {
	
	private final UUID id;

	public IdContainer(
			UUID id) {
		super();
		this.id = id;
	}
	
	public UUID getId() {
		return id;
	}
	

}
