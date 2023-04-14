package io.jstach.spec.mustache.spec.comments;

import io.jstach.spec.generator.SpecListing;
import java.util.Map;

public enum CommentsSpecTemplate implements SpecListing {

	INLINE(Inline.class, "comments", "Inline", "Comment blocks should be removed from the template.", "{}",
			"12345{{! Comment Block! }}67890", "1234567890", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new Inline();
			m.putAll(o);
			return InlineRenderer.of().execute(m);
		}
	},
	MULTILINE(Multiline.class, "comments", "Multiline", "Multiline comments should be permitted.", "{}",
			"12345{{!\n  This is a\n  multi-line comment...\n}}67890\n", "1234567890\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new Multiline();
			m.putAll(o);
			return MultilineRenderer.of().execute(m);
		}
	},
	STANDALONE(Standalone.class, "comments", "Standalone", "All standalone comment lines should be removed.", "{}",
			"Begin.\n{{! Comment Block! }}\nEnd.\n", "Begin.\nEnd.\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new Standalone();
			m.putAll(o);
			return StandaloneRenderer.of().execute(m);
		}
	},
	INDENTED_STANDALONE(IndentedStandalone.class, "comments", "Indented Standalone",
			"All standalone comment lines should be removed.", "{}", "Begin.\n  {{! Indented Comment Block! }}\nEnd.\n",
			"Begin.\nEnd.\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new IndentedStandalone();
			m.putAll(o);
			return IndentedStandaloneRenderer.of().execute(m);
		}
	},
	STANDALONE_LINE_ENDINGS(StandaloneLineEndings.class, "comments", "Standalone Line Endings",
			"\"\\r\\n\" should be considered a newline for standalone tags.", "{}",
			"|\r\n{{! Standalone Comment }}\r\n|", "|\r\n|", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new StandaloneLineEndings();
			m.putAll(o);
			return StandaloneLineEndingsRenderer.of().execute(m);
		}
	},
	STANDALONE_WITHOUT_PREVIOUS_LINE(StandaloneWithoutPreviousLine.class, "comments",
			"Standalone Without Previous Line", "Standalone tags should not require a newline to precede them.", "{}",
			"  {{! I'm Still Standalone }}\n!", "!", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new StandaloneWithoutPreviousLine();
			m.putAll(o);
			return StandaloneWithoutPreviousLineRenderer.of().execute(m);
		}
	},
	STANDALONE_WITHOUT_NEWLINE(StandaloneWithoutNewline.class, "comments", "Standalone Without Newline",
			"Standalone tags should not require a newline to follow them.", "{}", "!\n  {{! I'm Still Standalone }}",
			"!\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new StandaloneWithoutNewline();
			m.putAll(o);
			return StandaloneWithoutNewlineRenderer.of().execute(m);
		}
	},
	MULTILINE_STANDALONE(MultilineStandalone.class, "comments", "Multiline Standalone",
			"All standalone comment lines should be removed.", "{}",
			"Begin.\n{{!\nSomething's going on here...\n}}\nEnd.\n", "Begin.\nEnd.\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new MultilineStandalone();
			m.putAll(o);
			return MultilineStandaloneRenderer.of().execute(m);
		}
	},
	INDENTED_MULTILINE_STANDALONE(IndentedMultilineStandalone.class, "comments", "Indented Multiline Standalone",
			"All standalone comment lines should be removed.", "{}",
			"Begin.\n  {{!\n    Something's going on here...\n  }}\nEnd.\n", "Begin.\nEnd.\n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new IndentedMultilineStandalone();
			m.putAll(o);
			return IndentedMultilineStandaloneRenderer.of().execute(m);
		}
	},
	INDENTED_INLINE(IndentedInline.class, "comments", "Indented Inline", "Inline comments should not strip whitespace",
			"{}", "  12 {{! 34 }}\n", "  12 \n", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new IndentedInline();
			m.putAll(o);
			return IndentedInlineRenderer.of().execute(m);
		}
	},
	SURROUNDING_WHITESPACE(SurroundingWhitespace.class, "comments", "Surrounding Whitespace",
			"Comment removal should preserve surrounding whitespace.", "{}", "12345 {{! Comment Block! }} 67890",
			"12345  67890", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new SurroundingWhitespace();
			m.putAll(o);
			return SurroundingWhitespaceRenderer.of().execute(m);
		}
	},
	VARIABLE_NAME_COLLISION(VariableNameCollision.class, "comments", "Variable Name Collision",
			"Comments must never render, even if variable with same name exists.",
			"{\"! comment\":1,\"! comment \":2,\"!comment\":3,\"comment\":4}", "comments never show: >{{! comment }}<",
			"comments never show: ><", Map.of()) {
		public String render(Map<String, Object> o) {
			var m = new VariableNameCollision();
			m.putAll(o);
			return VariableNameCollisionRenderer.of().execute(m);
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

	private CommentsSpecTemplate(Class<?> modelClass, String group, String title, String description, String json,
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

	public static final String INLINE_FILE = "comments/Inline.mustache";

	public static final String MULTILINE_FILE = "comments/Multiline.mustache";

	public static final String STANDALONE_FILE = "comments/Standalone.mustache";

	public static final String INDENTED_STANDALONE_FILE = "comments/IndentedStandalone.mustache";

	public static final String STANDALONE_LINE_ENDINGS_FILE = "comments/StandaloneLineEndings.mustache";

	public static final String STANDALONE_WITHOUT_PREVIOUS_LINE_FILE = "comments/StandaloneWithoutPreviousLine.mustache";

	public static final String STANDALONE_WITHOUT_NEWLINE_FILE = "comments/StandaloneWithoutNewline.mustache";

	public static final String MULTILINE_STANDALONE_FILE = "comments/MultilineStandalone.mustache";

	public static final String INDENTED_MULTILINE_STANDALONE_FILE = "comments/IndentedMultilineStandalone.mustache";

	public static final String INDENTED_INLINE_FILE = "comments/IndentedInline.mustache";

	public static final String SURROUNDING_WHITESPACE_FILE = "comments/SurroundingWhitespace.mustache";

	public static final String VARIABLE_NAME_COLLISION_FILE = "comments/VariableNameCollision.mustache";

}
