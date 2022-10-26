package io.jstach;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.function.Consumer;

public interface RenderFunction extends Consumer<Appendable> {

	@Override
	default void accept(Appendable t) {
		try {
			render(t);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}

	}

	public void render(Appendable a) throws IOException;

	default String renderString() {
		return append(new StringBuilder()).toString();
	}

	default StringBuilder append(StringBuilder sb) {
		try {
			render(sb);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return sb;
	}

	default <A extends Appendable> A append(A a) throws IOException {
		render(a);
		return a;
	}

	public static RenderFunction of(List<? extends RenderFunction> rfs) {
		return new Composite(rfs);
	}

	static class Composite implements RenderFunction {

		private final List<? extends RenderFunction> functions;

		public Composite(List<? extends RenderFunction> functions) {
			super();
			this.functions = functions;
		}

		@Override
		public void render(Appendable a) throws IOException {
			for (var f : functions) {
				f.render(a);
			}
		}

	}

}
