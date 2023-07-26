package io.jstach.examples;

import java.util.UUID;

class IdContainer implements Mixin {

	public static IdContainer test() {
		return new IdContainer(UUID.nameUUIDFromBytes("test".getBytes()));
	}

	private final UUID id;

	public IdContainer(UUID id) {
		super();
		this.id = id;
	}

	public UUID id() {
		return id;
	}

}
