package io.jstach.examples;

import java.util.Map;

import io.jstach.jstache.JStache;

@JStache(path = "map.mustache")
record MapContainer(Map<String, String> fields, Map<String, MapContainer> containers) {

}
