package com.snaphop.staticmustache.spec.partials;

import com.snaphop.staticmustache.spec.SpecListing;
import java.util.Map;

public enum PartialsSpecTemplate implements SpecListing {
    BASIC_BEHAVIOR(
        null,
        "partials",
        "Basic Behavior",
        "The greater-than operator should expand to the named partial.",
        "{}",
        "\"{{>text}}\"",
        "\"from partial\""){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    FAILED_LOOKUP(
        null,
        "partials",
        "Failed Lookup",
        "The empty string should be used when the named partial is not found.",
        "{}",
        "\"{{>text}}\"",
        "\"\""){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    CONTEXT(
        null,
        "partials",
        "Context",
        "The greater-than operator should operate within the current context.",
        "{\"text\":\"content\"}",
        "\"{{>partial}}\"",
        "\"*content*\""){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    RECURSION(
        null,
        "partials",
        "Recursion",
        "The greater-than operator should properly recurse.",
        "{\"content\":\"X\",\"nodes\":[{\"content\":\"Y\",\"nodes\":[]}]}",
        "{{>node}}",
        "X<Y<>>"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    SURROUNDING_WHITESPACE(
        null,
        "partials",
        "Surrounding Whitespace",
        "The greater-than operator should not alter surrounding whitespace.",
        "{}",
        "| {{>partial}} |",
        "| \t|\t |"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    INLINE_INDENTATION(
        null,
        "partials",
        "Inline Indentation",
        "Whitespace should be left untouched.",
        "{\"data\":\"|\"}",
        "  {{data}}  {{> partial}}\n",
        "  |  >\n>\n"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    STANDALONE_LINE_ENDINGS(
        null,
        "partials",
        "Standalone Line Endings",
        "\"\\r\\n\" should be considered a newline for standalone tags.",
        "{}",
        "|\r\n{{>partial}}\r\n|",
        "|\r\n>|"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    STANDALONE_WITHOUT_PREVIOUS_LINE(
        null,
        "partials",
        "Standalone Without Previous Line",
        "Standalone tags should not require a newline to precede them.",
        "{}",
        "  {{>partial}}\n>",
        "  >\n  >>"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    STANDALONE_WITHOUT_NEWLINE(
        null,
        "partials",
        "Standalone Without Newline",
        "Standalone tags should not require a newline to follow them.",
        "{}",
        ">\n  {{>partial}}",
        ">\n  >\n  >"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    STANDALONE_INDENTATION(
        null,
        "partials",
        "Standalone Indentation",
        "Each line of the partial should be indented before rendering.",
        "{\"content\":\"<\\n->\"}",
        "\\\n {{>partial}}\n/\n",
        "\\\n |\n <\n->\n |\n/\n"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    PADDING_WHITESPACE(
        null,
        "partials",
        "Padding Whitespace",
        "Superfluous in-tag whitespace should be ignored.",
        "{\"boolean\":true}",
        "|{{> partial }}|",
        "|[]|"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    ;
    private final Class<?> modelClass;
    private final String group;
    private final String title;
    private final String description;
    private final String json;
    private final String template;
    private final String expected;

    private PartialsSpecTemplate(
        Class<?> modelClass,
        String group,
        String title,
        String description,
        String json,
        String template,
        String expected) {
        this.modelClass = modelClass;
        this.group = group;
        this.title = title;
        this.description = description;
        this.json = json;
        this.template = template;
        this.expected = expected;
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
    public abstract String render(Map<String, Object> o);

    public static final String BASIC_BEHAVIOR_FILE = "partials/BasicBehavior.mustache";
    public static final String FAILED_LOOKUP_FILE = "partials/FailedLookup.mustache";
    public static final String CONTEXT_FILE = "partials/Context.mustache";
    public static final String RECURSION_FILE = "partials/Recursion.mustache";
    public static final String SURROUNDING_WHITESPACE_FILE = "partials/SurroundingWhitespace.mustache";
    public static final String INLINE_INDENTATION_FILE = "partials/InlineIndentation.mustache";
    public static final String STANDALONE_LINE_ENDINGS_FILE = "partials/StandaloneLineEndings.mustache";
    public static final String STANDALONE_WITHOUT_PREVIOUS_LINE_FILE = "partials/StandaloneWithoutPreviousLine.mustache";
    public static final String STANDALONE_WITHOUT_NEWLINE_FILE = "partials/StandaloneWithoutNewline.mustache";
    public static final String STANDALONE_INDENTATION_FILE = "partials/StandaloneIndentation.mustache";
    public static final String PADDING_WHITESPACE_FILE = "partials/PaddingWhitespace.mustache";
}
