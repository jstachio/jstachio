package com.github.sviperll.staticmustache.examples2;

import com.github.sviperll.staticmustache.GenerateRenderableAdapter;

@GenerateRenderableAdapter(template = "blah.mustache")
public class Blah {
    
    private final String name;

    public Blah(String name) {
        super();
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

}
