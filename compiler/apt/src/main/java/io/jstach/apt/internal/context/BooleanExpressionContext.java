package io.jstach.apt.internal.context;

import org.eclipse.jdt.annotation.Nullable;

public interface BooleanExpressionContext extends RenderingContext {

	@Nullable
	BooleanExpressionContext getParentExpression();

	String getExpression();

}
