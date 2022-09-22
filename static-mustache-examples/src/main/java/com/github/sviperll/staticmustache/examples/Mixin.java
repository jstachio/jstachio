package com.github.sviperll.staticmustache.examples;


public interface Mixin {
    
    default String csrf() {
        return "MyCsrf";
    }

}
