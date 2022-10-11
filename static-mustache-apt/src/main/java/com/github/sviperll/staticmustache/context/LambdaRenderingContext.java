package com.github.sviperll.staticmustache.context;

import org.eclipse.jdt.annotation.Nullable;

class LambdaRenderingContext implements RenderingContext {
    private final Lambda lambda;
    private final VariableContext variables;
    public LambdaRenderingContext(Lambda lambda, VariableContext variables) {
        this.lambda = lambda;
        this.variables = variables;
    }

    @Override
    public String beginSectionRenderingCode() {
       return "";
    }

    @Override
    public String endSectionRenderingCode() {
        return variables.unescapedWriter() + ".append(" + currentExpression().text() + ");";
    }
    
    @Override
    public @Nullable JavaExpression getDataDirectly(String name) throws ContextException {
        return null;
    }

    @Override
    public JavaExpression getDataOrDefault(String name, JavaExpression defaultValue) {
        return defaultValue;
    }

    @Override
    public JavaExpression currentExpression() {
        return lambda.callExpression();
    }

    @Override
    public VariableContext createEnclosedVariableContext() {
        return variables.createEnclosedContext();
    }
    
    @Override
    public @Nullable RenderingContext getParent() {
        return null;
    }
}
