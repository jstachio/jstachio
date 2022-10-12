package com.github.sviperll.staticmustache.context;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;

public class ContextNodeRenderingContext extends MapRenderingContext {

    ContextNodeRenderingContext(JavaExpression expression, TypeElement element, RenderingContext parent) {
        super(expression, element, parent);
    }
    
    @Override
    public JavaExpression find(String name) throws ContextException {
        //See MapNode.find
        //Currently this only works if MapNode is the root context
        var all = JavaLanguageModel.getInstance().getElements().getAllMembers(definitionElement);
        
        var getMethod = ElementFilter.methodsIn(all).stream()
                .filter(e -> "find".equals(e.getSimpleName().toString())
                        && e.getModifiers().contains(Modifier.PUBLIC) 
                        && ! e.getModifiers().contains(Modifier.STATIC)
                        && e.getReturnType().getKind() != TypeKind.VOID
                        && e.getParameters().size() == 1 ).findFirst().orElse(null);
        
        if (getMethod == null) {
            return null;
        }
        return expression.mapGet(getMethod, name);
    }

}
