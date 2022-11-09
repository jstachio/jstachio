package io.jstach.spec.mustache.spec.interpolation;

import java.util.Map;

import io.jstach.spec.generator.SpecListing;

public enum InterpolationSpecTemplate implements SpecListing {

	NO_INTERPOLATION(NoInterpolation.class, "interpolation", "No Interpolation",
			"Mustache-free templates should render as-is.", "{}", "Hello from {Mustache}!\n",
			"Hello from {Mustache}!\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new NoInterpolation();
			m.putAll(o);
			var r = NoInterpolationRenderer.of(m);
			return r.render();
		}
	},
	BASIC_INTERPOLATION(BasicInterpolation.class, "interpolation", "Basic Interpolation",
			"Unadorned tags should interpolate content into the template.", "{\"subject\":\"world\"}",
			"Hello, {{subject}}!\n", "Hello, world!\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new BasicInterpolation();
			m.putAll(o);
			var r = BasicInterpolationRenderer.of(m);
			return r.render();
		}
	},
	HTML_ESCAPING(HTMLEscaping.class, "interpolation", "HTML Escaping", "Basic interpolation should be HTML escaped.",
			"{\"forbidden\":\"& \\\" < >\"}", "These characters should be HTML escaped: {{forbidden}}\n",
			"These characters should be HTML escaped: &amp; &quot; &lt; &gt;\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new HTMLEscaping();
			m.putAll(o);
			var r = HTMLEscapingRenderer.of(m);
			return r.render();
		}
	},
	TRIPLE_MUSTACHE(TripleMustache.class, "interpolation", "Triple Mustache",
			"Triple mustaches should interpolate without HTML escaping.", "{\"forbidden\":\"& \\\" < >\"}",
			"These characters should not be HTML escaped: {{{forbidden}}}\n",
			"These characters should not be HTML escaped: & \" < >\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new TripleMustache();
			m.putAll(o);
			var r = TripleMustacheRenderer.of(m);
			return r.render();
		}
	},
	AMPERSAND(Ampersand.class, "interpolation", "Ampersand", "Ampersand should interpolate without HTML escaping.",
			"{\"forbidden\":\"& \\\" < >\"}", "These characters should not be HTML escaped: {{&forbidden}}\n",
			"These characters should not be HTML escaped: & \" < >\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new Ampersand();
			m.putAll(o);
			var r = AmpersandRenderer.of(m);
			return r.render();
		}
	},
	BASIC_INTEGER_INTERPOLATION(BasicIntegerInterpolation.class, "interpolation", "Basic Integer Interpolation",
			"Integers should interpolate seamlessly.", "{\"mph\":85}", "\"{{mph}} miles an hour!\"",
			"\"85 miles an hour!\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new BasicIntegerInterpolation();
			m.putAll(o);
			var r = BasicIntegerInterpolationRenderer.of(m);
			return r.render();
		}
	},
	TRIPLE_MUSTACHE_INTEGER_INTERPOLATION(TripleMustacheIntegerInterpolation.class, "interpolation",
			"Triple Mustache Integer Interpolation", "Integers should interpolate seamlessly.", "{\"mph\":85}",
			"\"{{{mph}}} miles an hour!\"", "\"85 miles an hour!\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new TripleMustacheIntegerInterpolation();
			m.putAll(o);
			var r = TripleMustacheIntegerInterpolationRenderer.of(m);
			return r.render();
		}
	},
	AMPERSAND_INTEGER_INTERPOLATION(AmpersandIntegerInterpolation.class, "interpolation",
			"Ampersand Integer Interpolation", "Integers should interpolate seamlessly.", "{\"mph\":85}",
			"\"{{&mph}} miles an hour!\"", "\"85 miles an hour!\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new AmpersandIntegerInterpolation();
			m.putAll(o);
			var r = AmpersandIntegerInterpolationRenderer.of(m);
			return r.render();
		}
	},
	BASIC_DECIMAL_INTERPOLATION(BasicDecimalInterpolation.class, "interpolation", "Basic Decimal Interpolation",
			"Decimals should interpolate seamlessly with proper significance.", "{\"power\":1.21}",
			"\"{{power}} jiggawatts!\"", "\"1.21 jiggawatts!\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new BasicDecimalInterpolation();
			m.putAll(o);
			var r = BasicDecimalInterpolationRenderer.of(m);
			return r.render();
		}
	},
	TRIPLE_MUSTACHE_DECIMAL_INTERPOLATION(TripleMustacheDecimalInterpolation.class, "interpolation",
			"Triple Mustache Decimal Interpolation", "Decimals should interpolate seamlessly with proper significance.",
			"{\"power\":1.21}", "\"{{{power}}} jiggawatts!\"", "\"1.21 jiggawatts!\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new TripleMustacheDecimalInterpolation();
			m.putAll(o);
			var r = TripleMustacheDecimalInterpolationRenderer.of(m);
			return r.render();
		}
	},
	AMPERSAND_DECIMAL_INTERPOLATION(AmpersandDecimalInterpolation.class, "interpolation",
			"Ampersand Decimal Interpolation", "Decimals should interpolate seamlessly with proper significance.",
			"{\"power\":1.21}", "\"{{&power}} jiggawatts!\"", "\"1.21 jiggawatts!\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new AmpersandDecimalInterpolation();
			m.putAll(o);
			var r = AmpersandDecimalInterpolationRenderer.of(m);
			return r.render();
		}
	},
	BASIC_NULL_INTERPOLATION(BasicNullInterpolation.class, "interpolation", "Basic Null Interpolation",
			"Nulls should interpolate as the empty string.", "{\"cannot\":null}", "I ({{cannot}}) be seen!",
			"I () be seen!", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new BasicNullInterpolation();
			m.putAll(o);
			var r = BasicNullInterpolationRenderer.of(m);
			return r.render();
		}
	},
	TRIPLE_MUSTACHE_NULL_INTERPOLATION(TripleMustacheNullInterpolation.class, "interpolation",
			"Triple Mustache Null Interpolation", "Nulls should interpolate as the empty string.", "{\"cannot\":null}",
			"I ({{{cannot}}}) be seen!", "I () be seen!", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new TripleMustacheNullInterpolation();
			m.putAll(o);
			var r = TripleMustacheNullInterpolationRenderer.of(m);
			return r.render();
		}
	},
	AMPERSAND_NULL_INTERPOLATION(AmpersandNullInterpolation.class, "interpolation", "Ampersand Null Interpolation",
			"Nulls should interpolate as the empty string.", "{\"cannot\":null}", "I ({{&cannot}}) be seen!",
			"I () be seen!", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new AmpersandNullInterpolation();
			m.putAll(o);
			var r = AmpersandNullInterpolationRenderer.of(m);
			return r.render();
		}
	},
	BASIC_CONTEXT_MISS_INTERPOLATION(BasicContextMissInterpolation.class, "interpolation",
			"Basic Context Miss Interpolation", "Failed context lookups should default to empty strings.", "{}",
			"I ({{cannot}}) be seen!", "I () be seen!", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new BasicContextMissInterpolation();
			m.putAll(o);
			var r = BasicContextMissInterpolationRenderer.of(m);
			return r.render();
		}
	},
	TRIPLE_MUSTACHE_CONTEXT_MISS_INTERPOLATION(TripleMustacheContextMissInterpolation.class, "interpolation",
			"Triple Mustache Context Miss Interpolation", "Failed context lookups should default to empty strings.",
			"{}", "I ({{{cannot}}}) be seen!", "I () be seen!", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new TripleMustacheContextMissInterpolation();
			m.putAll(o);
			var r = TripleMustacheContextMissInterpolationRenderer.of(m);
			return r.render();
		}
	},
	AMPERSAND_CONTEXT_MISS_INTERPOLATION(AmpersandContextMissInterpolation.class, "interpolation",
			"Ampersand Context Miss Interpolation", "Failed context lookups should default to empty strings.", "{}",
			"I ({{&cannot}}) be seen!", "I () be seen!", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new AmpersandContextMissInterpolation();
			m.putAll(o);
			var r = AmpersandContextMissInterpolationRenderer.of(m);
			return r.render();
		}
	},
	DOTTED_NAMES___BASIC_INTERPOLATION(DottedNamesBasicInterpolation.class, "interpolation",
			"Dotted Names - Basic Interpolation", "Dotted names should be considered a form of shorthand for sections.",
			"{\"person\":{\"name\":\"Joe\"}}", "\"{{person.name}}\" == \"{{#person}}{{name}}{{/person}}\"",
			"\"Joe\" == \"Joe\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new DottedNamesBasicInterpolation();
			m.putAll(o);
			var r = DottedNamesBasicInterpolationRenderer.of(m);
			return r.render();
		}
	},
	DOTTED_NAMES___TRIPLE_MUSTACHE_INTERPOLATION(DottedNamesTripleMustacheInterpolation.class, "interpolation",
			"Dotted Names - Triple Mustache Interpolation",
			"Dotted names should be considered a form of shorthand for sections.", "{\"person\":{\"name\":\"Joe\"}}",
			"\"{{{person.name}}}\" == \"{{#person}}{{{name}}}{{/person}}\"", "\"Joe\" == \"Joe\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new DottedNamesTripleMustacheInterpolation();
			m.putAll(o);
			var r = DottedNamesTripleMustacheInterpolationRenderer.of(m);
			return r.render();
		}
	},
	DOTTED_NAMES___AMPERSAND_INTERPOLATION(DottedNamesAmpersandInterpolation.class, "interpolation",
			"Dotted Names - Ampersand Interpolation",
			"Dotted names should be considered a form of shorthand for sections.", "{\"person\":{\"name\":\"Joe\"}}",
			"\"{{&person.name}}\" == \"{{#person}}{{&name}}{{/person}}\"", "\"Joe\" == \"Joe\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new DottedNamesAmpersandInterpolation();
			m.putAll(o);
			var r = DottedNamesAmpersandInterpolationRenderer.of(m);
			return r.render();
		}
	},
	DOTTED_NAMES___ARBITRARY_DEPTH(DottedNamesArbitraryDepth.class, "interpolation", "Dotted Names - Arbitrary Depth",
			"Dotted names should be functional to any level of nesting.",
			"{\"a\":{\"b\":{\"c\":{\"d\":{\"e\":{\"name\":\"Phil\"}}}}}}", "\"{{a.b.c.d.e.name}}\" == \"Phil\"",
			"\"Phil\" == \"Phil\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new DottedNamesArbitraryDepth();
			m.putAll(o);
			var r = DottedNamesArbitraryDepthRenderer.of(m);
			return r.render();
		}
	},
	DOTTED_NAMES___BROKEN_CHAINS(DottedNamesBrokenChains.class, "interpolation", "Dotted Names - Broken Chains",
			"Any falsey value prior to the last part of the name should yield ''.", "{\"a\":{}}",
			"\"{{a.b.c}}\" == \"\"", "\"\" == \"\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new DottedNamesBrokenChains();
			m.putAll(o);
			var r = DottedNamesBrokenChainsRenderer.of(m);
			return r.render();
		}
	},
	DOTTED_NAMES___BROKEN_CHAIN_RESOLUTION(DottedNamesBrokenChainResolution.class, "interpolation",
			"Dotted Names - Broken Chain Resolution",
			"Each part of a dotted name should resolve only against its parent.",
			"{\"a\":{\"b\":{}},\"c\":{\"name\":\"Jim\"}}", "\"{{a.b.c.name}}\" == \"\"", "\"\" == \"\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new DottedNamesBrokenChainResolution();
			m.putAll(o);
			var r = DottedNamesBrokenChainResolutionRenderer.of(m);
			return r.render();
		}
	},
	DOTTED_NAMES___INITIAL_RESOLUTION(DottedNamesInitialResolution.class, "interpolation",
			"Dotted Names - Initial Resolution", "The first part of a dotted name should resolve as any other name.",
			"{\"a\":{\"b\":{\"c\":{\"d\":{\"e\":{\"name\":\"Phil\"}}}}},\"b\":{\"c\":{\"d\":{\"e\":{\"name\":\"Wrong\"}}}}}",
			"\"{{#a}}{{b.c.d.e.name}}{{/a}}\" == \"Phil\"", "\"Phil\" == \"Phil\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new DottedNamesInitialResolution();
			m.putAll(o);
			var r = DottedNamesInitialResolutionRenderer.of(m);
			return r.render();
		}
	},
	DOTTED_NAMES___CONTEXT_PRECEDENCE(DottedNamesContextPrecedence.class, "interpolation",
			"Dotted Names - Context Precedence", "Dotted names should be resolved against former resolutions.",
			"{\"a\":{\"b\":{}},\"b\":{\"c\":\"ERROR\"}}", "{{#a}}{{b.c}}{{/a}}", "", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new DottedNamesContextPrecedence();
			m.putAll(o);
			var r = DottedNamesContextPrecedenceRenderer.of(m);
			return r.render();
		}
	},
	INTERPOLATION___SURROUNDING_WHITESPACE(InterpolationSurroundingWhitespace.class, "interpolation",
			"Interpolation - Surrounding Whitespace", "Interpolation should not alter surrounding whitespace.",
			"{\"string\":\"---\"}", "| {{string}} |", "| --- |", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new InterpolationSurroundingWhitespace();
			m.putAll(o);
			var r = InterpolationSurroundingWhitespaceRenderer.of(m);
			return r.render();
		}
	},
	TRIPLE_MUSTACHE___SURROUNDING_WHITESPACE(TripleMustacheSurroundingWhitespace.class, "interpolation",
			"Triple Mustache - Surrounding Whitespace", "Interpolation should not alter surrounding whitespace.",
			"{\"string\":\"---\"}", "| {{{string}}} |", "| --- |", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new TripleMustacheSurroundingWhitespace();
			m.putAll(o);
			var r = TripleMustacheSurroundingWhitespaceRenderer.of(m);
			return r.render();
		}
	},
	AMPERSAND___SURROUNDING_WHITESPACE(AmpersandSurroundingWhitespace.class, "interpolation",
			"Ampersand - Surrounding Whitespace", "Interpolation should not alter surrounding whitespace.",
			"{\"string\":\"---\"}", "| {{&string}} |", "| --- |", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new AmpersandSurroundingWhitespace();
			m.putAll(o);
			var r = AmpersandSurroundingWhitespaceRenderer.of(m);
			return r.render();
		}
	},
	INTERPOLATION___STANDALONE(InterpolationStandalone.class, "interpolation", "Interpolation - Standalone",
			"Standalone interpolation should not alter surrounding whitespace.", "{\"string\":\"---\"}",
			"  {{string}}\n", "  ---\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new InterpolationStandalone();
			m.putAll(o);
			var r = InterpolationStandaloneRenderer.of(m);
			return r.render();
		}
	},
	TRIPLE_MUSTACHE___STANDALONE(TripleMustacheStandalone.class, "interpolation", "Triple Mustache - Standalone",
			"Standalone interpolation should not alter surrounding whitespace.", "{\"string\":\"---\"}",
			"  {{{string}}}\n", "  ---\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new TripleMustacheStandalone();
			m.putAll(o);
			var r = TripleMustacheStandaloneRenderer.of(m);
			return r.render();
		}
	},
	AMPERSAND___STANDALONE(AmpersandStandalone.class, "interpolation", "Ampersand - Standalone",
			"Standalone interpolation should not alter surrounding whitespace.", "{\"string\":\"---\"}",
			"  {{&string}}\n", "  ---\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new AmpersandStandalone();
			m.putAll(o);
			var r = AmpersandStandaloneRenderer.of(m);
			return r.render();
		}
	},
	INTERPOLATION_WITH_PADDING(InterpolationWithPadding.class, "interpolation", "Interpolation With Padding",
			"Superfluous in-tag whitespace should be ignored.", "{\"string\":\"---\"}", "|{{ string }}|", "|---|",
			Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new InterpolationWithPadding();
			m.putAll(o);
			var r = InterpolationWithPaddingRenderer.of(m);
			return r.render();
		}
	},
	TRIPLE_MUSTACHE_WITH_PADDING(TripleMustacheWithPadding.class, "interpolation", "Triple Mustache With Padding",
			"Superfluous in-tag whitespace should be ignored.", "{\"string\":\"---\"}", "|{{{ string }}}|", "|---|",
			Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new TripleMustacheWithPadding();
			m.putAll(o);
			var r = TripleMustacheWithPaddingRenderer.of(m);
			return r.render();
		}
	},
	AMPERSAND_WITH_PADDING(AmpersandWithPadding.class, "interpolation", "Ampersand With Padding",
			"Superfluous in-tag whitespace should be ignored.", "{\"string\":\"---\"}", "|{{& string }}|", "|---|",
			Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new AmpersandWithPadding();
			m.putAll(o);
			var r = AmpersandWithPaddingRenderer.of(m);
			return r.render();
		}
	},;

	private final Class<?> modelClass;

	private final String group;

	private final String title;

	private final String description;

	private final String json;

	private final String template;

	private final String expected;

	private final Map<String, String> partials;

	private InterpolationSpecTemplate(Class<?> modelClass, String group, String title, String description, String json,
			String template, String expected, Map<String, String> partials) {
		this.modelClass = modelClass;
		this.group = group;
		this.title = title;
		this.description = description;
		this.json = json;
		this.template = template;
		this.expected = expected;
		this.partials = partials;
	}

	public Class<?> modelClass() {
		return modelClass;
	}

	public String group() {
		return this.group;
	}

	public String title() {
		return this.title;
	}

	public String description() {
		return this.description;
	}

	public String template() {
		return this.template;
	}

	public String json() {
		return this.json;
	}

	public String expected() {
		return this.expected;
	}

	public boolean enabled() {
		return modelClass != null;
	}

	public Map<String, String> partials() {
		return this.partials;
	}

	public abstract String render(Map<String, Object> o);

	public static final String NO_INTERPOLATION_FILE = "interpolation/NoInterpolation.mustache";

	public static final String BASIC_INTERPOLATION_FILE = "interpolation/BasicInterpolation.mustache";

	public static final String HTML_ESCAPING_FILE = "interpolation/HTMLEscaping.mustache";

	public static final String TRIPLE_MUSTACHE_FILE = "interpolation/TripleMustache.mustache";

	public static final String AMPERSAND_FILE = "interpolation/Ampersand.mustache";

	public static final String BASIC_INTEGER_INTERPOLATION_FILE = "interpolation/BasicIntegerInterpolation.mustache";

	public static final String TRIPLE_MUSTACHE_INTEGER_INTERPOLATION_FILE = "interpolation/TripleMustacheIntegerInterpolation.mustache";

	public static final String AMPERSAND_INTEGER_INTERPOLATION_FILE = "interpolation/AmpersandIntegerInterpolation.mustache";

	public static final String BASIC_DECIMAL_INTERPOLATION_FILE = "interpolation/BasicDecimalInterpolation.mustache";

	public static final String TRIPLE_MUSTACHE_DECIMAL_INTERPOLATION_FILE = "interpolation/TripleMustacheDecimalInterpolation.mustache";

	public static final String AMPERSAND_DECIMAL_INTERPOLATION_FILE = "interpolation/AmpersandDecimalInterpolation.mustache";

	public static final String BASIC_NULL_INTERPOLATION_FILE = "interpolation/BasicNullInterpolation.mustache";

	public static final String TRIPLE_MUSTACHE_NULL_INTERPOLATION_FILE = "interpolation/TripleMustacheNullInterpolation.mustache";

	public static final String AMPERSAND_NULL_INTERPOLATION_FILE = "interpolation/AmpersandNullInterpolation.mustache";

	public static final String BASIC_CONTEXT_MISS_INTERPOLATION_FILE = "interpolation/BasicContextMissInterpolation.mustache";

	public static final String TRIPLE_MUSTACHE_CONTEXT_MISS_INTERPOLATION_FILE = "interpolation/TripleMustacheContextMissInterpolation.mustache";

	public static final String AMPERSAND_CONTEXT_MISS_INTERPOLATION_FILE = "interpolation/AmpersandContextMissInterpolation.mustache";

	public static final String DOTTED_NAMES___BASIC_INTERPOLATION_FILE = "interpolation/DottedNamesBasicInterpolation.mustache";

	public static final String DOTTED_NAMES___TRIPLE_MUSTACHE_INTERPOLATION_FILE = "interpolation/DottedNamesTripleMustacheInterpolation.mustache";

	public static final String DOTTED_NAMES___AMPERSAND_INTERPOLATION_FILE = "interpolation/DottedNamesAmpersandInterpolation.mustache";

	public static final String DOTTED_NAMES___ARBITRARY_DEPTH_FILE = "interpolation/DottedNamesArbitraryDepth.mustache";

	public static final String DOTTED_NAMES___BROKEN_CHAINS_FILE = "interpolation/DottedNamesBrokenChains.mustache";

	public static final String DOTTED_NAMES___BROKEN_CHAIN_RESOLUTION_FILE = "interpolation/DottedNamesBrokenChainResolution.mustache";

	public static final String DOTTED_NAMES___INITIAL_RESOLUTION_FILE = "interpolation/DottedNamesInitialResolution.mustache";

	public static final String DOTTED_NAMES___CONTEXT_PRECEDENCE_FILE = "interpolation/DottedNamesContextPrecedence.mustache";

	public static final String INTERPOLATION___SURROUNDING_WHITESPACE_FILE = "interpolation/InterpolationSurroundingWhitespace.mustache";

	public static final String TRIPLE_MUSTACHE___SURROUNDING_WHITESPACE_FILE = "interpolation/TripleMustacheSurroundingWhitespace.mustache";

	public static final String AMPERSAND___SURROUNDING_WHITESPACE_FILE = "interpolation/AmpersandSurroundingWhitespace.mustache";

	public static final String INTERPOLATION___STANDALONE_FILE = "interpolation/InterpolationStandalone.mustache";

	public static final String TRIPLE_MUSTACHE___STANDALONE_FILE = "interpolation/TripleMustacheStandalone.mustache";

	public static final String AMPERSAND___STANDALONE_FILE = "interpolation/AmpersandStandalone.mustache";

	public static final String INTERPOLATION_WITH_PADDING_FILE = "interpolation/InterpolationWithPadding.mustache";

	public static final String TRIPLE_MUSTACHE_WITH_PADDING_FILE = "interpolation/TripleMustacheWithPadding.mustache";

	public static final String AMPERSAND_WITH_PADDING_FILE = "interpolation/AmpersandWithPadding.mustache";

}
