package io.jstach.apt;

import io.jstach.apt.internal.MustacheToken.TagToken;
import io.jstach.apt.internal.Position;
import io.jstach.apt.internal.token.MustacheTagKind;

record Section<T> (TagToken token, Position position, T data) {
	Section {
		if (!token.tagKind().isBeginSection()) {
			throw new IllegalArgumentException("bug should be a begin section");
		}
	}

	String name() {
		return token.name();
	}

	boolean isBlock() {
		return token.tagKind() == MustacheTagKind.BEGIN_BLOCK_SECTION;
	}
}