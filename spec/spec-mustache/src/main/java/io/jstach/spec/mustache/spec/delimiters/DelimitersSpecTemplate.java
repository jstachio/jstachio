package io.jstach.spec.mustache.spec.delimiters;

import io.jstach.spec.generator.SpecListing;
import java.util.Map;

public enum DelimitersSpecTemplate implements SpecListing {

	PAIR_BEHAVIOR(PairBehavior.class, "delimiters", "Pair Behavior",
			"The equals sign (used on both sides) should permit delimiter changes.", "{\"text\":\"Hey!\"}",
			"{{=<% %>=}}(<%text%>)", "(Hey!)", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new PairBehavior();
			m.putAll(o);
			return PairBehaviorRenderer.of().execute(m);
		}
	},
	SPECIAL_CHARACTERS(SpecialCharacters.class, "delimiters", "Special Characters",
			"Characters with special meaning regexen should be valid delimiters.", "{\"text\":\"It worked!\"}",
			"({{=[ ]=}}[text])", "(It worked!)", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new SpecialCharacters();
			m.putAll(o);
			return SpecialCharactersRenderer.of().execute(m);
		}
	},
	SECTIONS(Sections.class, "delimiters", "Sections", "Delimiters set outside sections should persist.",
			"{\"section\":true,\"data\":\"I got interpolated.\"}",
			"[\n{{#section}}\n  {{data}}\n  |data|\n{{/section}}\n\n{{= | | =}}\n|#section|\n  {{data}}\n  |data|\n|/section|\n]\n",
			"[\n  I got interpolated.\n  |data|\n\n  {{data}}\n  I got interpolated.\n]\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new Sections();
			m.putAll(o);
			return SectionsRenderer.of().execute(m);
		}
	},
	INVERTED_SECTIONS(InvertedSections.class, "delimiters", "Inverted Sections",
			"Delimiters set outside inverted sections should persist.",
			"{\"section\":false,\"data\":\"I got interpolated.\"}",
			"[\n{{^section}}\n  {{data}}\n  |data|\n{{/section}}\n\n{{= | | =}}\n|^section|\n  {{data}}\n  |data|\n|/section|\n]\n",
			"[\n  I got interpolated.\n  |data|\n\n  {{data}}\n  I got interpolated.\n]\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new InvertedSections();
			m.putAll(o);
			return InvertedSectionsRenderer.of().execute(m);
		}
	},
	PARTIAL_INHERITENCE(PartialInheritence.class, "delimiters", "Partial Inheritence",
			"Delimiters set in a parent template should not affect a partial.", "{\"value\":\"yes\"}",
			"[ {{>include}} ]\n{{= | | =}}\n[ |>include| ]\n", "[ .yes. ]\n[ .yes. ]\n", Map.of(

					"include", ".{{value}}.")) {
		public String render(Map<String, Object> o) {
			var m = new PartialInheritence();
			m.putAll(o);
			return PartialInheritenceRenderer.of().execute(m);
		}
	},
	POST_PARTIAL_BEHAVIOR(PostPartialBehavior.class, "delimiters", "Post-Partial Behavior",
			"Delimiters set in a partial should not affect the parent template.", "{\"value\":\"yes\"}",
			"[ {{>include}} ]\n[ .{{value}}.  .|value|. ]\n", "[ .yes.  .yes. ]\n[ .yes.  .|value|. ]\n", Map.of(

					"include", ".{{value}}. {{= | | =}} .|value|.")) {
		public String render(Map<String, Object> o) {
			var m = new PostPartialBehavior();
			m.putAll(o);
			return PostPartialBehaviorRenderer.of().execute(m);
		}
	},
	SURROUNDING_WHITESPACE(SurroundingWhitespace.class, "delimiters", "Surrounding Whitespace",
			"Surrounding whitespace should be left untouched.", "{}", "| {{=@ @=}} |", "|  |", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new SurroundingWhitespace();
			m.putAll(o);
			return SurroundingWhitespaceRenderer.of().execute(m);
		}
	},
	OUTLYING_WHITESPACE__INLINE_(OutlyingWhitespaceInline.class, "delimiters", "Outlying Whitespace (Inline)",
			"Whitespace should be left untouched.", "{}", " | {{=@ @=}}\n", " | \n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new OutlyingWhitespaceInline();
			m.putAll(o);
			return OutlyingWhitespaceInlineRenderer.of().execute(m);
		}
	},
	STANDALONE_TAG(StandaloneTag.class, "delimiters", "Standalone Tag",
			"Standalone lines should be removed from the template.", "{}", "Begin.\n{{=@ @=}}\nEnd.\n",
			"Begin.\nEnd.\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new StandaloneTag();
			m.putAll(o);
			return StandaloneTagRenderer.of().execute(m);
		}
	},
	INDENTED_STANDALONE_TAG(IndentedStandaloneTag.class, "delimiters", "Indented Standalone Tag",
			"Indented standalone lines should be removed from the template.", "{}", "Begin.\n  {{=@ @=}}\nEnd.\n",
			"Begin.\nEnd.\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new IndentedStandaloneTag();
			m.putAll(o);
			return IndentedStandaloneTagRenderer.of().execute(m);
		}
	},
	STANDALONE_LINE_ENDINGS(StandaloneLineEndings.class, "delimiters", "Standalone Line Endings",
			"\"\\r\\n\" should be considered a newline for standalone tags.", "{}", "|\r\n{{= @ @ =}}\r\n|", "|\r\n|",
			Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new StandaloneLineEndings();
			m.putAll(o);
			return StandaloneLineEndingsRenderer.of().execute(m);
		}
	},
	STANDALONE_WITHOUT_PREVIOUS_LINE(StandaloneWithoutPreviousLine.class, "delimiters",
			"Standalone Without Previous Line", "Standalone tags should not require a newline to precede them.", "{}",
			"  {{=@ @=}}\n=", "=", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new StandaloneWithoutPreviousLine();
			m.putAll(o);
			return StandaloneWithoutPreviousLineRenderer.of().execute(m);
		}
	},
	STANDALONE_WITHOUT_NEWLINE(StandaloneWithoutNewline.class, "delimiters", "Standalone Without Newline",
			"Standalone tags should not require a newline to follow them.", "{}", "=\n  {{=@ @=}}", "=\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new StandaloneWithoutNewline();
			m.putAll(o);
			return StandaloneWithoutNewlineRenderer.of().execute(m);
		}
	},
	PAIR_WITH_PADDING(PairwithPadding.class, "delimiters", "Pair with Padding",
			"Superfluous in-tag whitespace should be ignored.", "{}", "|{{= @   @ =}}|", "||", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new PairwithPadding();
			m.putAll(o);
			return PairwithPaddingRenderer.of().execute(m);
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

	private DelimitersSpecTemplate(Class<?> modelClass, String group, String title, String description, String json,
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

	public static final String PAIR_BEHAVIOR_FILE = "delimiters/PairBehavior.mustache";

	public static final String SPECIAL_CHARACTERS_FILE = "delimiters/SpecialCharacters.mustache";

	public static final String SECTIONS_FILE = "delimiters/Sections.mustache";

	public static final String INVERTED_SECTIONS_FILE = "delimiters/InvertedSections.mustache";

	public static final String PARTIAL_INHERITENCE_FILE = "delimiters/PartialInheritence.mustache";

	public static final String POST_PARTIAL_BEHAVIOR_FILE = "delimiters/PostPartialBehavior.mustache";

	public static final String SURROUNDING_WHITESPACE_FILE = "delimiters/SurroundingWhitespace.mustache";

	public static final String OUTLYING_WHITESPACE__INLINE__FILE = "delimiters/OutlyingWhitespaceInline.mustache";

	public static final String STANDALONE_TAG_FILE = "delimiters/StandaloneTag.mustache";

	public static final String INDENTED_STANDALONE_TAG_FILE = "delimiters/IndentedStandaloneTag.mustache";

	public static final String STANDALONE_LINE_ENDINGS_FILE = "delimiters/StandaloneLineEndings.mustache";

	public static final String STANDALONE_WITHOUT_PREVIOUS_LINE_FILE = "delimiters/StandaloneWithoutPreviousLine.mustache";

	public static final String STANDALONE_WITHOUT_NEWLINE_FILE = "delimiters/StandaloneWithoutNewline.mustache";

	public static final String PAIR_WITH_PADDING_FILE = "delimiters/PairwithPadding.mustache";

}
