package io.jstach.examples;

import java.util.Map;

import io.jstach.annotation.JStach;

@JStach(path="map.mustache")
public record MapContainer(Map<String, String> fields, Map<String, MapContainer> containers) {

}
