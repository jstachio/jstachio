package io.jstach;

import java.io.IOException;

public interface JStacheMixin {

	default void render(Appendable a) throws IOException {
		JStachio.render(this, a);
	}

	default StringBuilder render(StringBuilder sb) {
		return JStachio.render(this, sb);
	}

	default String render() {
		return JStachio.render(this);
	}

}
