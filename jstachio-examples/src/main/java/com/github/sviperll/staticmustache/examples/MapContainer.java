package com.github.sviperll.staticmustache.examples;

import java.util.Map;

import io.jstach.GenerateRenderableAdapter;

@GenerateRenderableAdapter(template="map.mustache")
public record MapContainer(Map<String, String> fields, Map<String, MapContainer> containers) {

}