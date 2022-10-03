package com.github.sviperll.staticmustache.examples;

import java.io.IOException;

import com.github.sviperll.staticmustache.MapNode;
import com.github.sviperll.staticmustache.TemplateLambda;
import com.github.sviperll.staticmustache.text.RenderFunction;

public record Lambda(String name) {
    
    @TemplateLambda
    public String prefix(String n) {
        return "Sir " + n;
    }
    
    @TemplateLambda
    public void suffix(Appendable a, RenderFunction block) throws IOException {
        block.accept(a);
        a.append(" Esquire");
    }
    
    public void something(MapNode context, RenderFunction block, String text) {
        
    }

}
