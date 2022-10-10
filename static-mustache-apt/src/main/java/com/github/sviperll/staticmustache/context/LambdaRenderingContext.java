package com.github.sviperll.staticmustache.context;

import org.eclipse.jdt.annotation.Nullable;

class LambdaRenderingContext implements RenderingContext {
    private final JavaExpression expression;
    private final VariableContext variables;
    public LambdaRenderingContext(JavaExpression expression, VariableContext variables) {
        this.expression = expression;
        this.variables = variables;
    }

    @Override
    public String beginSectionRenderingCode() {
        return variables.unescapedWriter() + ".append(" + expression.text() + ");";
    }

    @Override
    public String endSectionRenderingCode() {
        return "";
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
        return expression;
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
