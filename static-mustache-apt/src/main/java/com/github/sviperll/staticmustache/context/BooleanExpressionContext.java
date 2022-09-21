package com.github.sviperll.staticmustache.context;

import org.jspecify.nullness.Nullable;

public interface BooleanExpressionContext extends RenderingContext {

    @Nullable
    BooleanExpressionContext getParentExpression();
    
    String getExpression();

}
