package io.jstach.examples.fragment;

import io.jstach.jstache.JStache;

@JStache(path = "fragment.mustache#fragment-a")
public record FragmentExample(String message) {

	@JStache(template = """
			{{> fragment.mustache#fragment-a }}
			""")
	public record FragmentPartial(String message) {

	}

	@JStache(path = "fragment.mustache#c")
	public record FragmentC(String message) {

	}
}
