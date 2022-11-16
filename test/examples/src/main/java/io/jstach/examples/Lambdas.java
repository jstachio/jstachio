package io.jstach.examples;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.jstach.jstache.JStacheLambda;
import io.jstach.jstache.JStacheLambda.Raw;

interface Lambdas {

	@JStacheLambda
	default @Raw String listProps(@Raw String body, Map<String, String> props) {
		return props.entrySet().stream().map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n"));
	}

	@JStacheLambda
	default KeyValues eachProps(Map<String, String> props) {
		var kvs = props.entrySet().stream().map(e -> new KeyValue(e.getKey(), e.getValue())).toList();
		return new KeyValues(kvs);
	}

	record KeyValues(List<KeyValue> values) {

	}

	record KeyValue(String key, String value) {

	}

}
