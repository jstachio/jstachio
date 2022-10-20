package io.jstach.examples;


public interface Mixin {
    
    default String csrf() {
        return "MyCsrf";
    }

}
