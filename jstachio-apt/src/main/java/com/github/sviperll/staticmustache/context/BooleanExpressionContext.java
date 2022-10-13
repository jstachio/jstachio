package com.github.sviperll.staticmustache.context;

import org.eclipse.jdt.annotation.Nullable;

public interface BooleanExpressionContext extends RenderingContext {

    @Nullable
    BooleanExpressionContext getParentExpression();
    
    String getExpression();

}
