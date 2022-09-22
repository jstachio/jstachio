package com.github.sviperll.staticmustache.examples;

import java.util.UUID;

public class IdContainer implements Mixin {
	
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
