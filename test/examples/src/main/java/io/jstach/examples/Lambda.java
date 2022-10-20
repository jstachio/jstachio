package io.jstach.examples;

import java.io.IOException;

import io.jstach.RenderFunction;
import io.jstach.annotation.JStacheLambda;
import io.jstach.context.ContextNode;

public record Lambda(String name) {
    
    @JStacheLambda
    public String prefix(String n) {
        return "Sir " + n;
    }
    
    @JStacheLambda
    public void suffix(Appendable a, RenderFunction block) throws IOException {
        block.accept(a);
        a.append(" Esquire");
    }
    
    public void something(ContextNode context, RenderFunction block, String text) {
        
    }

}
