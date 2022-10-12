package com.github.sviperll.staticmustache.context;

import org.eclipse.jdt.annotation.Nullable;

class LambdaRenderingContext implements RenderingContext {
    private final Lambda lambda;
    private final VariableContext variables;
    private final RenderingContext parent;
    
    public LambdaRenderingContext(
            Lambda lambda, 
            VariableContext variables,
            RenderingContext parent) {
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
        //return variables.unescapedWriter() + ".append(" + currentExpression().text() + ");";
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
        return lambda.callExpression("ignore");
    }

    @Override
    public VariableContext createEnclosedVariableContext() {
        return variables.createEnclosedContext();
    }
    
    @Override
    public @Nullable RenderingContext getParent() {
        return parent;
    }
    
}
