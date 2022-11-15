package io.jstach.spec.mustache.spec.inverted;

import io.jstach.spec.generator.SpecListing;
import java.util.Map;

public enum InvertedSpecTemplate implements SpecListing {

	FALSEY(Falsey.class, "inverted", "Falsey", "Falsey sections should have their contents rendered.",
			"{\"boolean\":false}", "\"{{^boolean}}This should be rendered.{{/boolean}}\"",
			"\"This should be rendered.\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new Falsey();
			m.putAll(o);
			return FalseyRenderer.of().execute(m);
		}
	},
	TRUTHY(Truthy.class, "inverted", "Truthy", "Truthy sections should have their contents omitted.",
			"{\"boolean\":true}", "\"{{^boolean}}This should not be rendered.{{/boolean}}\"", "\"\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new Truthy();
			m.putAll(o);
			return TruthyRenderer.of().execute(m);
		}
	},
	NULL_IS_FALSEY(Nullisfalsey.class, "inverted", "Null is falsey", "Null is falsey.", "{\"null\":null}",
			"\"{{^null}}This should be rendered.{{/null}}\"", "\"This should be rendered.\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new Nullisfalsey();
			m.putAll(o);
			return NullisfalseyRenderer.of().execute(m);
		}
	},
	CONTEXT(Context.class, "inverted", "Context", "Objects and hashes should behave like truthy values.",
			"{\"context\":{\"name\":\"Joe\"}}", "\"{{^context}}Hi {{name}}.{{/context}}\"", "\"\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new Context();
			m.putAll(o);
			return ContextRenderer.of().execute(m);
		}
	},
	LIST(List.class, "inverted", "List", "Lists should behave like truthy values.",
			"{\"list\":[{\"n\":1},{\"n\":2},{\"n\":3}]}", "\"{{^list}}{{n}}{{/list}}\"", "\"\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new List();
			m.putAll(o);
			return ListRenderer.of().execute(m);
		}
	},
	EMPTY_LIST(EmptyList.class, "inverted", "Empty List", "Empty lists should behave like falsey values.",
			"{\"list\":[]}", "\"{{^list}}Yay lists!{{/list}}\"", "\"Yay lists!\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new EmptyList();
			m.putAll(o);
			return EmptyListRenderer.of().execute(m);
		}
	},
	DOUBLED(Doubled.class, "inverted", "Doubled", "Multiple inverted sections per template should be permitted.",
			"{\"bool\":false,\"two\":\"second\"}",
			"{{^bool}}\n* first\n{{/bool}}\n* {{two}}\n{{^bool}}\n* third\n{{/bool}}\n", "* first\n* second\n* third\n",
			Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new Doubled();
			m.putAll(o);
			return DoubledRenderer.of().execute(m);
		}
	},
	NESTED__FALSEY_(NestedFalsey.class, "inverted", "Nested (Falsey)",
			"Nested falsey sections should have their contents rendered.", "{\"bool\":false}",
			"| A {{^bool}}B {{^bool}}C{{/bool}} D{{/bool}} E |", "| A B C D E |", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new NestedFalsey();
			m.putAll(o);
			return NestedFalseyRenderer.of().execute(m);
		}
	},
	NESTED__TRUTHY_(NestedTruthy.class, "inverted", "Nested (Truthy)", "Nested truthy sections should be omitted.",
			"{\"bool\":true}", "| A {{^bool}}B {{^bool}}C{{/bool}} D{{/bool}} E |", "| A  E |", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new NestedTruthy();
			m.putAll(o);
			return NestedTruthyRenderer.of().execute(m);
		}
	},
	CONTEXT_MISSES(ContextMisses.class, "inverted", "Context Misses",
			"Failed context lookups should be considered falsey.", "{}",
			"[{{^missing}}Cannot find key 'missing'!{{/missing}}]", "[Cannot find key 'missing'!]", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new ContextMisses();
			m.putAll(o);
			return ContextMissesRenderer.of().execute(m);
		}
	},
	DOTTED_NAMES___TRUTHY(DottedNamesTruthy.class, "inverted", "Dotted Names - Truthy",
			"Dotted names should be valid for Inverted Section tags.", "{\"a\":{\"b\":{\"c\":true}}}",
			"\"{{^a.b.c}}Not Here{{/a.b.c}}\" == \"\"", "\"\" == \"\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new DottedNamesTruthy();
			m.putAll(o);
			return DottedNamesTruthyRenderer.of().execute(m);
		}
	},
	DOTTED_NAMES___FALSEY(DottedNamesFalsey.class, "inverted", "Dotted Names - Falsey",
			"Dotted names should be valid for Inverted Section tags.", "{\"a\":{\"b\":{\"c\":false}}}",
			"\"{{^a.b.c}}Not Here{{/a.b.c}}\" == \"Not Here\"", "\"Not Here\" == \"Not Here\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new DottedNamesFalsey();
			m.putAll(o);
			return DottedNamesFalseyRenderer.of().execute(m);
		}
	},
	DOTTED_NAMES___BROKEN_CHAINS(DottedNamesBrokenChains.class, "inverted", "Dotted Names - Broken Chains",
			"Dotted names that cannot be resolved should be considered falsey.", "{\"a\":{}}",
			"\"{{^a.b.c}}Not Here{{/a.b.c}}\" == \"Not Here\"", "\"Not Here\" == \"Not Here\"", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new DottedNamesBrokenChains();
			m.putAll(o);
			return DottedNamesBrokenChainsRenderer.of().execute(m);
		}
	},
	SURROUNDING_WHITESPACE(SurroundingWhitespace.class, "inverted", "Surrounding Whitespace",
			"Inverted sections should not alter surrounding whitespace.", "{\"boolean\":false}",
			" | {{^boolean}}\t|\t{{/boolean}} | \n", " | \t|\t | \n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new SurroundingWhitespace();
			m.putAll(o);
			return SurroundingWhitespaceRenderer.of().execute(m);
		}
	},
	INTERNAL_WHITESPACE(InternalWhitespace.class, "inverted", "Internal Whitespace",
			"Inverted should not alter internal whitespace.", "{\"boolean\":false}",
			" | {{^boolean}} {{! Important Whitespace }}\n {{/boolean}} | \n", " |  \n  | \n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new InternalWhitespace();
			m.putAll(o);
			return InternalWhitespaceRenderer.of().execute(m);
		}
	},
	INDENTED_INLINE_SECTIONS(IndentedInlineSections.class, "inverted", "Indented Inline Sections",
			"Single-line sections should not alter surrounding whitespace.", "{\"boolean\":false}",
			" {{^boolean}}NO{{/boolean}}\n {{^boolean}}WAY{{/boolean}}\n", " NO\n WAY\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new IndentedInlineSections();
			m.putAll(o);
			return IndentedInlineSectionsRenderer.of().execute(m);
		}
	},
	STANDALONE_LINES(StandaloneLines.class, "inverted", "Standalone Lines",
			"Standalone lines should be removed from the template.", "{\"boolean\":false}",
			"| This Is\n{{^boolean}}\n|\n{{/boolean}}\n| A Line\n", "| This Is\n|\n| A Line\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new StandaloneLines();
			m.putAll(o);
			return StandaloneLinesRenderer.of().execute(m);
		}
	},
	STANDALONE_INDENTED_LINES(StandaloneIndentedLines.class, "inverted", "Standalone Indented Lines",
			"Standalone indented lines should be removed from the template.", "{\"boolean\":false}",
			"| This Is\n  {{^boolean}}\n|\n  {{/boolean}}\n| A Line\n", "| This Is\n|\n| A Line\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new StandaloneIndentedLines();
			m.putAll(o);
			return StandaloneIndentedLinesRenderer.of().execute(m);
		}
	},
	STANDALONE_LINE_ENDINGS(StandaloneLineEndings.class, "inverted", "Standalone Line Endings",
			"\"\\r\\n\" should be considered a newline for standalone tags.", "{\"boolean\":false}",
			"|\r\n{{^boolean}}\r\n{{/boolean}}\r\n|", "|\r\n|", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new StandaloneLineEndings();
			m.putAll(o);
			return StandaloneLineEndingsRenderer.of().execute(m);
		}
	},
	STANDALONE_WITHOUT_PREVIOUS_LINE(StandaloneWithoutPreviousLine.class, "inverted",
			"Standalone Without Previous Line", "Standalone tags should not require a newline to precede them.",
			"{\"boolean\":false}", "  {{^boolean}}\n^{{/boolean}}\n/", "^\n/", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new StandaloneWithoutPreviousLine();
			m.putAll(o);
			return StandaloneWithoutPreviousLineRenderer.of().execute(m);
		}
	},
	STANDALONE_WITHOUT_NEWLINE(StandaloneWithoutNewline.class, "inverted", "Standalone Without Newline",
			"Standalone tags should not require a newline to follow them.", "{\"boolean\":false}",
			"^{{^boolean}}\n/\n  {{/boolean}}", "^\n/\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new StandaloneWithoutNewline();
			m.putAll(o);
			return StandaloneWithoutNewlineRenderer.of().execute(m);
		}
	},
	PADDING(Padding.class, "inverted", "Padding", "Superfluous in-tag whitespace should be ignored.",
			"{\"boolean\":false}", "|{{^ boolean }}={{/ boolean }}|", "|=|", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new Padding();
			m.putAll(o);
			return PaddingRenderer.of().execute(m);
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

	private InvertedSpecTemplate(Class<?> modelClass, String group, String title, String description, String json,
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

	public static final String FALSEY_FILE = "inverted/Falsey.mustache";

	public static final String TRUTHY_FILE = "inverted/Truthy.mustache";

	public static final String NULL_IS_FALSEY_FILE = "inverted/Nullisfalsey.mustache";

	public static final String CONTEXT_FILE = "inverted/Context.mustache";

	public static final String LIST_FILE = "inverted/List.mustache";

	public static final String EMPTY_LIST_FILE = "inverted/EmptyList.mustache";

	public static final String DOUBLED_FILE = "inverted/Doubled.mustache";

	public static final String NESTED__FALSEY__FILE = "inverted/NestedFalsey.mustache";

	public static final String NESTED__TRUTHY__FILE = "inverted/NestedTruthy.mustache";

	public static final String CONTEXT_MISSES_FILE = "inverted/ContextMisses.mustache";

	public static final String DOTTED_NAMES___TRUTHY_FILE = "inverted/DottedNamesTruthy.mustache";

	public static final String DOTTED_NAMES___FALSEY_FILE = "inverted/DottedNamesFalsey.mustache";

	public static final String DOTTED_NAMES___BROKEN_CHAINS_FILE = "inverted/DottedNamesBrokenChains.mustache";

	public static final String SURROUNDING_WHITESPACE_FILE = "inverted/SurroundingWhitespace.mustache";

	public static final String INTERNAL_WHITESPACE_FILE = "inverted/InternalWhitespace.mustache";

	public static final String INDENTED_INLINE_SECTIONS_FILE = "inverted/IndentedInlineSections.mustache";

	public static final String STANDALONE_LINES_FILE = "inverted/StandaloneLines.mustache";

	public static final String STANDALONE_INDENTED_LINES_FILE = "inverted/StandaloneIndentedLines.mustache";

	public static final String STANDALONE_LINE_ENDINGS_FILE = "inverted/StandaloneLineEndings.mustache";

	public static final String STANDALONE_WITHOUT_PREVIOUS_LINE_FILE = "inverted/StandaloneWithoutPreviousLine.mustache";

	public static final String STANDALONE_WITHOUT_NEWLINE_FILE = "inverted/StandaloneWithoutNewline.mustache";

	public static final String PADDING_FILE = "inverted/Padding.mustache";

}
