package com.github.sviperll.staticmustache.context;

import java.util.Map;

import javax.lang.model.element.ExecutableElement;

import org.eclipse.jdt.annotation.Nullable;

public sealed interface Lambda {

    public String name();
    
    public JavaExpression expression();
    
    public ExecutableElement method();
    
    default JavaExpression callExpression(String literalBlock) {
        return expression().methodCall(method(), expression().stringLiteral(literalBlock));
    }
    
    public enum ReturnType {
        INLINE_TEMPLATE,
        PATH_TEMPLATE,
        STRING,
        RENDER_FUNCTION
    }

    public record InlineTemplateLambda(
            JavaExpression expression,
            ExecutableElement method, 
            String name, 
            String template) implements Lambda {

    }

    public record PathTemplateLambda(
            JavaExpression expression,
            ExecutableElement method, 
            String name, 
            String path) implements Lambda {

    }

    public record StringLambda(
            JavaExpression expression,
            ExecutableElement method, 
            String name) implements Lambda {
    }
    
    public static Lambda of( //
            JavaExpression expression,
            ExecutableElement method, 
            @Nullable String name, 
            @Nullable String template, 
            @Nullable String path) {
        if (name == null || name.isBlank()) {
            name = method.getSimpleName().toString();
        }
        if (template != null && ! template.isBlank()) {
            return new InlineTemplateLambda(expression, method, name, template);
        }
        if (path != null && ! path.isBlank()) {
            return new PathTemplateLambda(expression, method, name, path);
        }
        
        if (JavaLanguageModel.getInstance().isSameType(method.getReturnType(), 
                JavaLanguageModel.getInstance().knownTypes()._String.typeElement().asType())) { 
            return new StringLambda(expression, method, name);
        }
        
        throw new IllegalStateException("method is not supported. method = " + method);
    }
    
    public class Lambdas {
        
        private final Map<String, Lambda> lambdas;
        
        private LambdaCallback callback = (lb, v) -> {
            throw new IllegalStateException("callback not set");
        };
        
        public Lambdas(Map<String, Lambda> lambdas) {
            super();
            this.lambdas = lambdas;
        }
        
        public void setCallback(LambdaCallback callback) {
            this.callback = callback;
        }
        
        
        public LambdaCallback getCallback() {
            return callback;
        }
        
        public Map<String, Lambda> lambdas() {
            return lambdas;
        }
        
    }
    
    public interface LambdaCallback {
        public JavaExpression apply(Lambda lambda, VariableContext variables);
    }
}
