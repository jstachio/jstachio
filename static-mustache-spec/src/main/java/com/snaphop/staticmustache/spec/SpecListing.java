package com.snaphop.staticmustache.spec;

import java.io.UncheckedIOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface SpecListing {
    
    String json();
    
    String expected();
    
    String render(Map<String, Object> o);
    
    String title();
    
    default String render() {
        return render(createContext());
    }
    
    @SuppressWarnings("unchecked")
    default Map<String, Object> createContext() {
        try {
            return (Map<String,Object>) new ObjectMapper().readValue(json(), Map.class);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        } 
    }

}
