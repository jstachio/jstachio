package io.jstach.examples;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class MapStructExample {
    
    public abstract Stuff convert(Stuff2 e);
    
    public static class Stuff {
        public String name;
    }
    
    public static class Stuff2 {
        public String name;
    }
    
    public static MapStructExample of() { 
        return Mappers.getMapper(MapStructExample.class);
    }
}
