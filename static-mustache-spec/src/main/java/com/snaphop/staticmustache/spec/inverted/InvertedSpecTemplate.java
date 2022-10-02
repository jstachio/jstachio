package com.snaphop.staticmustache.spec.inverted;

import com.snaphop.staticmustache.spec.SpecListing;
import java.util.Map;

public enum InvertedSpecTemplate implements SpecListing {
    FALSEY(
        Falsey.class,
        "inverted",
        "Falsey",
        "Falsey sections should have their contents rendered.",
        "{\"boolean\":false}",
        "\"{{^boolean}}This should be rendered.{{/boolean}}\"",
        "\"This should be rendered.\""){
        public String render(Map<String, Object> o) {
            var m = new Falsey();
            m.putAll(o);
            var r = FalseyRenderer.of(m);
            return r.renderString();
        }
    },
    TRUTHY(
        Truthy.class,
        "inverted",
        "Truthy",
        "Truthy sections should have their contents omitted.",
        "{\"boolean\":true}",
        "\"{{^boolean}}This should not be rendered.{{/boolean}}\"",
        "\"\""){
        public String render(Map<String, Object> o) {
            var m = new Truthy();
            m.putAll(o);
            var r = TruthyRenderer.of(m);
            return r.renderString();
        }
    },
    NULL_IS_FALSEY(
        Nullisfalsey.class,
        "inverted",
        "Null is falsey",
        "Null is falsey.",
        "{\"null\":null}",
        "\"{{^null}}This should be rendered.{{/null}}\"",
        "\"This should be rendered.\""){
        public String render(Map<String, Object> o) {
            var m = new Nullisfalsey();
            m.putAll(o);
            var r = NullisfalseyRenderer.of(m);
            return r.renderString();
        }
    },
    CONTEXT(
        Context.class,
        "inverted",
        "Context",
        "Objects and hashes should behave like truthy values.",
        "{\"context\":{\"name\":\"Joe\"}}",
        "\"{{^context}}Hi {{name}}.{{/context}}\"",
        "\"\""){
        public String render(Map<String, Object> o) {
            var m = new Context();
            m.putAll(o);
            var r = ContextRenderer.of(m);
            return r.renderString();
        }
    },
    LIST(
        List.class,
        "inverted",
        "List",
        "Lists should behave like truthy values.",
        "{\"list\":[{\"n\":1},{\"n\":2},{\"n\":3}]}",
        "\"{{^list}}{{n}}{{/list}}\"",
        "\"\""){
        public String render(Map<String, Object> o) {
            var m = new List();
            m.putAll(o);
            var r = ListRenderer.of(m);
            return r.renderString();
        }
    },
    EMPTY_LIST(
        EmptyList.class,
        "inverted",
        "Empty List",
        "Empty lists should behave like falsey values.",
        "{\"list\":[]}",
        "\"{{^list}}Yay lists!{{/list}}\"",
        "\"Yay lists!\""){
        public String render(Map<String, Object> o) {
            var m = new EmptyList();
            m.putAll(o);
            var r = EmptyListRenderer.of(m);
            return r.renderString();
        }
    },
    DOUBLED(
        Doubled.class,
        "inverted",
        "Doubled",
        "Multiple inverted sections per template should be permitted.",
        "{\"bool\":false,\"two\":\"second\"}",
        "{{^bool}}\n* first\n{{/bool}}\n* {{two}}\n{{^bool}}\n* third\n{{/bool}}\n",
        "* first\n* second\n* third\n"){
        public String render(Map<String, Object> o) {
            var m = new Doubled();
            m.putAll(o);
            var r = DoubledRenderer.of(m);
            return r.renderString();
        }
    },
    NESTED__FALSEY_(
        NestedFalsey.class,
        "inverted",
        "Nested (Falsey)",
        "Nested falsey sections should have their contents rendered.",
        "{\"bool\":false}",
        "| A {{^bool}}B {{^bool}}C{{/bool}} D{{/bool}} E |",
        "| A B C D E |"){
        public String render(Map<String, Object> o) {
            var m = new NestedFalsey();
            m.putAll(o);
            var r = NestedFalseyRenderer.of(m);
            return r.renderString();
        }
    },
    NESTED__TRUTHY_(
        NestedTruthy.class,
        "inverted",
        "Nested (Truthy)",
        "Nested truthy sections should be omitted.",
        "{\"bool\":true}",
        "| A {{^bool}}B {{^bool}}C{{/bool}} D{{/bool}} E |",
        "| A  E |"){
        public String render(Map<String, Object> o) {
            var m = new NestedTruthy();
            m.putAll(o);
            var r = NestedTruthyRenderer.of(m);
            return r.renderString();
        }
    },
    CONTEXT_MISSES(
        ContextMisses.class,
        "inverted",
        "Context Misses",
        "Failed context lookups should be considered falsey.",
        "{}",
        "[{{^missing}}Cannot find key 'missing'!{{/missing}}]",
        "[Cannot find key 'missing'!]"){
        public String render(Map<String, Object> o) {
            var m = new ContextMisses();
            m.putAll(o);
            var r = ContextMissesRenderer.of(m);
            return r.renderString();
        }
    },
    DOTTED_NAMES___TRUTHY(
        DottedNamesTruthy.class,
        "inverted",
        "Dotted Names - Truthy",
        "Dotted names should be valid for Inverted Section tags.",
        "{\"a\":{\"b\":{\"c\":true}}}",
        "\"{{^a.b.c}}Not Here{{/a.b.c}}\" == \"\"",
        "\"\" == \"\""){
        public String render(Map<String, Object> o) {
            var m = new DottedNamesTruthy();
            m.putAll(o);
            var r = DottedNamesTruthyRenderer.of(m);
            return r.renderString();
        }
    },
    DOTTED_NAMES___FALSEY(
        DottedNamesFalsey.class,
        "inverted",
        "Dotted Names - Falsey",
        "Dotted names should be valid for Inverted Section tags.",
        "{\"a\":{\"b\":{\"c\":false}}}",
        "\"{{^a.b.c}}Not Here{{/a.b.c}}\" == \"Not Here\"",
        "\"Not Here\" == \"Not Here\""){
        public String render(Map<String, Object> o) {
            var m = new DottedNamesFalsey();
            m.putAll(o);
            var r = DottedNamesFalseyRenderer.of(m);
            return r.renderString();
        }
    },
    DOTTED_NAMES___BROKEN_CHAINS(
        DottedNamesBrokenChains.class,
        "inverted",
        "Dotted Names - Broken Chains",
        "Dotted names that cannot be resolved should be considered falsey.",
        "{\"a\":{}}",
        "\"{{^a.b.c}}Not Here{{/a.b.c}}\" == \"Not Here\"",
        "\"Not Here\" == \"Not Here\""){
        public String render(Map<String, Object> o) {
            var m = new DottedNamesBrokenChains();
            m.putAll(o);
            var r = DottedNamesBrokenChainsRenderer.of(m);
            return r.renderString();
        }
    },
    SURROUNDING_WHITESPACE(
        SurroundingWhitespace.class,
        "inverted",
        "Surrounding Whitespace",
        "Inverted sections should not alter surrounding whitespace.",
        "{\"boolean\":false}",
        " | {{^boolean}}\t|\t{{/boolean}} | \n",
        " | \t|\t | \n"){
        public String render(Map<String, Object> o) {
            var m = new SurroundingWhitespace();
            m.putAll(o);
            var r = SurroundingWhitespaceRenderer.of(m);
            return r.renderString();
        }
    },
    INTERNAL_WHITESPACE(
        InternalWhitespace.class,
        "inverted",
        "Internal Whitespace",
        "Inverted should not alter internal whitespace.",
        "{\"boolean\":false}",
        " | {{^boolean}} {{! Important Whitespace }}\n {{/boolean}} | \n",
        " |  \n  | \n"){
        public String render(Map<String, Object> o) {
            var m = new InternalWhitespace();
            m.putAll(o);
            var r = InternalWhitespaceRenderer.of(m);
            return r.renderString();
        }
    },
    INDENTED_INLINE_SECTIONS(
        IndentedInlineSections.class,
        "inverted",
        "Indented Inline Sections",
        "Single-line sections should not alter surrounding whitespace.",
        "{\"boolean\":false}",
        " {{^boolean}}NO{{/boolean}}\n {{^boolean}}WAY{{/boolean}}\n",
        " NO\n WAY\n"){
        public String render(Map<String, Object> o) {
            var m = new IndentedInlineSections();
            m.putAll(o);
            var r = IndentedInlineSectionsRenderer.of(m);
            return r.renderString();
        }
    },
    STANDALONE_LINES(
        StandaloneLines.class,
        "inverted",
        "Standalone Lines",
        "Standalone lines should be removed from the template.",
        "{\"boolean\":false}",
        "| This Is\n{{^boolean}}\n|\n{{/boolean}}\n| A Line\n",
        "| This Is\n|\n| A Line\n"){
        public String render(Map<String, Object> o) {
            var m = new StandaloneLines();
            m.putAll(o);
            var r = StandaloneLinesRenderer.of(m);
            return r.renderString();
        }
    },
    STANDALONE_INDENTED_LINES(
        StandaloneIndentedLines.class,
        "inverted",
        "Standalone Indented Lines",
        "Standalone indented lines should be removed from the template.",
        "{\"boolean\":false}",
        "| This Is\n  {{^boolean}}\n|\n  {{/boolean}}\n| A Line\n",
        "| This Is\n|\n| A Line\n"){
        public String render(Map<String, Object> o) {
            var m = new StandaloneIndentedLines();
            m.putAll(o);
            var r = StandaloneIndentedLinesRenderer.of(m);
            return r.renderString();
        }
    },
    STANDALONE_LINE_ENDINGS(
        StandaloneLineEndings.class,
        "inverted",
        "Standalone Line Endings",
        "\"\\r\\n\" should be considered a newline for standalone tags.",
        "{\"boolean\":false}",
        "|\r\n{{^boolean}}\r\n{{/boolean}}\r\n|",
        "|\r\n|"){
        public String render(Map<String, Object> o) {
            var m = new StandaloneLineEndings();
            m.putAll(o);
            var r = StandaloneLineEndingsRenderer.of(m);
            return r.renderString();
        }
    },
    STANDALONE_WITHOUT_PREVIOUS_LINE(
        StandaloneWithoutPreviousLine.class,
        "inverted",
        "Standalone Without Previous Line",
        "Standalone tags should not require a newline to precede them.",
        "{\"boolean\":false}",
        "  {{^boolean}}\n^{{/boolean}}\n/",
        "^\n/"){
        public String render(Map<String, Object> o) {
            var m = new StandaloneWithoutPreviousLine();
            m.putAll(o);
            var r = StandaloneWithoutPreviousLineRenderer.of(m);
            return r.renderString();
        }
    },
    STANDALONE_WITHOUT_NEWLINE(
        StandaloneWithoutNewline.class,
        "inverted",
        "Standalone Without Newline",
        "Standalone tags should not require a newline to follow them.",
        "{\"boolean\":false}",
        "^{{^boolean}}\n/\n  {{/boolean}}",
        "^\n/\n"){
        public String render(Map<String, Object> o) {
            var m = new StandaloneWithoutNewline();
            m.putAll(o);
            var r = StandaloneWithoutNewlineRenderer.of(m);
            return r.renderString();
        }
    },
    PADDING(
        Padding.class,
        "inverted",
        "Padding",
        "Superfluous in-tag whitespace should be ignored.",
        "{\"boolean\":false}",
        "|{{^ boolean }}={{/ boolean }}|",
        "|=|"){
        public String render(Map<String, Object> o) {
            var m = new Padding();
            m.putAll(o);
            var r = PaddingRenderer.of(m);
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

    private InvertedSpecTemplate(
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
