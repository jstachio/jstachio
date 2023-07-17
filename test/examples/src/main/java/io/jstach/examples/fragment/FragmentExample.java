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

	/*
	 * dolar sign one
	 */
	@JStache(path = "fragment.mustache#d")
	public record FragmentD(String message) {

	}

	/*
	 * This will fail for now
	 */
	// @JStache(template = """
	// {{$blah}}
	// REPEAT
	// {{/blah}}
	// {{> #blah }}
	// """)
	// public record FragmentInline(String message) {
	//
	// }
}
