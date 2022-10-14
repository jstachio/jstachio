package io.jstach.examples;

import java.util.Map;

import io.jstach.annotation.GenerateRenderer;

@GenerateRenderer(template="map.mustache")
public record MapContainer(Map<String, String> fields, Map<String, MapContainer> containers) {

}
