package com.snaphop.staticmustache.spec.partials;

import com.snaphop.staticmustache.spec.SpecListing;
import java.util.Map;

public enum PartialsSpecTemplate implements SpecListing {
    BASIC_BEHAVIOR(
        BasicBehavior.class,
        "partials",
        "Basic Behavior",
        "The greater-than operator should expand to the named partial.",
        "{}",
        "\"{{>text}}\"",
        "\"from partial\"",
        Map.of(
            
            "text",
            "from partial"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new BasicBehavior();
            m.putAll(o);
            var r = BasicBehaviorRenderer.of(m);
            return r.renderString();
        }
    },
    FAILED_LOOKUP(
        null,
        "partials",
        "Failed Lookup",
        "The empty string should be used when the named partial is not found.",
        "{}",
        "\"{{>text}}\"",
        "\"\"",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    CONTEXT(
        Context.class,
        "partials",
        "Context",
        "The greater-than operator should operate within the current context.",
        "{\"text\":\"content\"}",
        "\"{{>partial}}\"",
        "\"*content*\"",
        Map.of(
            
            "partial",
            "*{{text}}*"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new Context();
            m.putAll(o);
            var r = ContextRenderer.of(m);
            return r.renderString();
        }
    },
    RECURSION(
        null,
        "partials",
        "Recursion",
        "The greater-than operator should properly recurse.",
        "{\"content\":\"X\",\"nodes\":[{\"content\":\"Y\",\"nodes\":[]}]}",
        "{{>node}}",
        "X<Y<>>",
        Map.of(
            
            "node",
            "{{content}}<{{#nodes}}{{>node}}{{/nodes}}>"
        )
        ){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    SURROUNDING_WHITESPACE(
        SurroundingWhitespace.class,
        "partials",
        "Surrounding Whitespace",
        "The greater-than operator should not alter surrounding whitespace.",
        "{}",
        "| {{>partial}} |",
        "| \t|\t |",
        Map.of(
            
            "partial",
            "\t|\t"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new SurroundingWhitespace();
            m.putAll(o);
            var r = SurroundingWhitespaceRenderer.of(m);
            return r.renderString();
        }
    },
    INLINE_INDENTATION(
        InlineIndentation.class,
        "partials",
        "Inline Indentation",
        "Whitespace should be left untouched.",
        "{\"data\":\"|\"}",
        "  {{data}}  {{> partial}}\n",
        "  |  >\n>\n",
        Map.of(
            
            "partial",
            ">\n>"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new InlineIndentation();
            m.putAll(o);
            var r = InlineIndentationRenderer.of(m);
            return r.renderString();
        }
    },
    STANDALONE_LINE_ENDINGS(
        StandaloneLineEndings.class,
        "partials",
        "Standalone Line Endings",
        "\"\\r\\n\" should be considered a newline for standalone tags.",
        "{}",
        "|\r\n{{>partial}}\r\n|",
        "|\r\n>|",
        Map.of(
            
            "partial",
            ">"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new StandaloneLineEndings();
            m.putAll(o);
            var r = StandaloneLineEndingsRenderer.of(m);
            return r.renderString();
        }
    },
    STANDALONE_WITHOUT_PREVIOUS_LINE(
        StandaloneWithoutPreviousLine.class,
        "partials",
        "Standalone Without Previous Line",
        "Standalone tags should not require a newline to precede them.",
        "{}",
        "  {{>partial}}\n>",
        "  >\n  >>",
        Map.of(
            
            "partial",
            ">\n>"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new StandaloneWithoutPreviousLine();
            m.putAll(o);
            var r = StandaloneWithoutPreviousLineRenderer.of(m);
            return r.renderString();
        }
    },
    STANDALONE_WITHOUT_NEWLINE(
        StandaloneWithoutNewline.class,
        "partials",
        "Standalone Without Newline",
        "Standalone tags should not require a newline to follow them.",
        "{}",
        ">\n  {{>partial}}",
        ">\n  >\n  >",
        Map.of(
            
            "partial",
            ">\n>"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new StandaloneWithoutNewline();
            m.putAll(o);
            var r = StandaloneWithoutNewlineRenderer.of(m);
            return r.renderString();
        }
    },
    STANDALONE_INDENTATION(
        StandaloneIndentation.class,
        "partials",
        "Standalone Indentation",
        "Each line of the partial should be indented before rendering.",
        "{\"content\":\"<\\n->\"}",
        "\\\n {{>partial}}\n/\n",
        "\\\n |\n <\n->\n |\n/\n",
        Map.of(
            
            "partial",
            "|\n{{{content}}}\n|\n"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new StandaloneIndentation();
            m.putAll(o);
            var r = StandaloneIndentationRenderer.of(m);
            return r.renderString();
        }
    },
    PADDING_WHITESPACE(
        PaddingWhitespace.class,
        "partials",
        "Padding Whitespace",
        "Superfluous in-tag whitespace should be ignored.",
        "{\"boolean\":true}",
        "|{{> partial }}|",
        "|[]|",
        Map.of(
            
            "partial",
            "[]"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new PaddingWhitespace();
            m.putAll(o);
            var r = PaddingWhitespaceRenderer.of(m);
            return r.renderString();
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
    private final Map<String,String> partials;

    private PartialsSpecTemplate(
        Class<?> modelClass,
        String group,
        String title,
        String description,
        String json,
        String template,
        String expected,
        Map<String,String> partials) {
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
    public Map<String,String> partials() {
        return this.partials;
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
