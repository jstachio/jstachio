package com.github.sviperll.staticmustache.examples2;

import java.util.UUID;

import io.jstach.annotation.GenerateRenderableAdapter;

@GenerateRenderableAdapter(template = "blah.mustache")
public class Blah {
    
    private final String name;
    private final UUID id;
    private final SomeUnknownType unknown;

    public Blah(String name, UUID id, SomeUnknownType unknown) {
        super();
        this.name = name;
        this.id = id;
        this.unknown = unknown;
    }
    
    public String getName() {
        return name;
    }
    
    
    public UUID getId() {
        return id;
    }
    
    public SomeUnknownType getUnknown() {
        return unknown;
    }
}
