package io.jstach.spec.generator;

import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samskivert.mustache.Mustache;

public interface SpecListing {

	String name();

	String group();

	String json();

	String expected();

	String render(Map<String, Object> o);

	String title();

	String description();

	String template();

	Class<?> modelClass();

	boolean enabled();

	Map<String, String> partials();

	default List<SpecPartial> specPartials() {
		return partials().entrySet().stream().map(e -> new SpecPartial(e.getKey(), e.getValue())).toList();
	}

	default String render() {
		return render(createContext());
	}

	@SuppressWarnings("unchecked")
	default Map<String, Object> createContext() {
		try {
			return (Map<String, Object>) new ObjectMapper().readValue(json(), Map.class);
		}
		catch (JsonProcessingException e) {
			throw new UncheckedIOException(e);
		}
	}

	default String describe() {

		String message = """
				---
				Group: {{group}}
				Name: {{name}}
				Title: {{title}}

				Desc: {{description}}

				json: {{json}}

				<partials>
				{{#specPartials}}
				    <{{name}}>{{path}}</{{name}}>
				{{/specPartials}}
				</partials>

				<template>{{template}}</template>

				<expected>{{expected}}</expected>
				""";
		return Mustache.compiler().escapeHTML(false).compile(message).execute(this);

	}

}
