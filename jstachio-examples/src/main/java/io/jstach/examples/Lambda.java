package io.jstach.examples;

import java.io.IOException;

import io.jstach.RenderFunction;
import io.jstach.annotation.JStachLambda;
import io.jstach.context.ContextNode;

public record Lambda(String name) {
    
    @JStachLambda
    public String prefix(String n) {
        return "Sir " + n;
    }
    
    @JStachLambda
    public void suffix(Appendable a, RenderFunction block) throws IOException {
        block.accept(a);
        a.append(" Esquire");
    }
    
    public void something(ContextNode context, RenderFunction block, String text) {
        
    }

}
