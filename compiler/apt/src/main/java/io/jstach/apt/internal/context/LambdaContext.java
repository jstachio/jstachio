package io.jstach.apt.internal.context;

public class LambdaContext {

	private final RenderingContext parent;

	public LambdaContext(LambdaRenderingContext parent) {
		super();
		this.parent = parent;
	}

	// public @Nullable JavaExpression get(String name) throws ContextException {
	// return parent.get(name);
	// }
	//
	// public @Nullable JavaExpression find(String name) throws ContextException {
	// return parent.find(name, (c) -> true);
	// }

	public JavaExpression get() {
		return parent.get();
	}

}
