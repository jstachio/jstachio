package io.jstach.examples;

import java.util.Map;

import io.jstach.annotation.GenerateRenderableAdapter;

@GenerateRenderableAdapter(template="map.mustache")
public record MapContainer(Map<String, String> fields, Map<String, MapContainer> containers) {

}
