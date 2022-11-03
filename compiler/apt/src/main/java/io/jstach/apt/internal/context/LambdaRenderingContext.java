package io.jstach.apt.internal.context;

import org.eclipse.jdt.annotation.Nullable;

class LambdaRenderingContext implements RenderingContext {

	private final Lambda lambda;

	private final VariableContext variables;

	private final RenderingContext parent;

	public LambdaRenderingContext(Lambda lambda, VariableContext variables, RenderingContext parent) {
		this.lambda = lambda;
		this.variables = variables;
		this.parent = parent;
	}

	public Lambda getLambda() {
		return lambda;
	}

	@Override
	public String beginSectionRenderingCode() {
		return "";
	}

	@Override
	public String endSectionRenderingCode() {
		return "";
	}

	@Override
	public @Nullable JavaExpression get(String name) throws ContextException {
		return parent.get(name);
	}

	@Override
	public JavaExpression get() {
		return parent.get();
	}

	@Override
	public JavaExpression currentExpression() {
		return parent.currentExpression();
		// var ctx = new LambdaContext(this);
		// return lambda.callExpression("NOT KNOWN YET", ctx);
	}

	@Override
	public VariableContext createEnclosedVariableContext() {
		return variables.createEnclosedContext();
	}

	@Override
	public RenderingContext getParent() {
		return parent;
	}

}
