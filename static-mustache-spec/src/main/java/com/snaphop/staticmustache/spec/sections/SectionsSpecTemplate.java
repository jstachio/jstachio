package com.snaphop.staticmustache.spec.sections;

import com.snaphop.staticmustache.spec.SpecListing;
import java.util.Map;

public enum SectionsSpecTemplate implements SpecListing {
    TRUTHY(
        Truthy.class,
        "Truthy",
        "{\"boolean\":true}",
        "\"This should be rendered.\""){
        public String render(Map<String, Object> o) {
            var m = new Truthy();
            m.putAll(o);
            var r = TruthyRenderer.of(m);
            return r.renderString();
        }
    },
    FALSEY(
        Falsey.class,
        "Falsey",
        "{\"boolean\":false}",
        "\"\""){
        public String render(Map<String, Object> o) {
            var m = new Falsey();
            m.putAll(o);
            var r = FalseyRenderer.of(m);
            return r.renderString();
        }
    },
    NULL_IS_FALSEY(
        Nullisfalsey.class,
        "Null is falsey",
        "{\"null\":null}",
        "\"\""){
        public String render(Map<String, Object> o) {
            var m = new Nullisfalsey();
            m.putAll(o);
            var r = NullisfalseyRenderer.of(m);
            return r.renderString();
        }
    },
    CONTEXT(
        Context.class,
        "Context",
        "{\"context\":{\"name\":\"Joe\"}}",
        "\"Hi Joe.\""){
        public String render(Map<String, Object> o) {
            var m = new Context();
            m.putAll(o);
            var r = ContextRenderer.of(m);
            return r.renderString();
        }
    },
    PARENT_CONTEXTS(
        Parentcontexts.class,
        "Parent contexts",
        "{\"a\":\"foo\",\"b\":\"wrong\",\"sec\":{\"b\":\"bar\"},\"c\":{\"d\":\"baz\"}}",
        "\"foo, bar, baz\""){
        public String render(Map<String, Object> o) {
            var m = new Parentcontexts();
            m.putAll(o);
            var r = ParentcontextsRenderer.of(m);
            return r.renderString();
        }
    },
    VARIABLE_TEST(
        Variabletest.class,
        "Variable test",
        "{\"foo\":\"bar\"}",
        "\"bar is bar\""){
        public String render(Map<String, Object> o) {
            var m = new Variabletest();
            m.putAll(o);
            var r = VariabletestRenderer.of(m);
            return r.renderString();
        }
    },
    LIST_CONTEXTS(
        ListContexts.class,
        "List Contexts",
        "{\"tops\":[{\"tname\":{\"upper\":\"A\",\"lower\":\"a\"},\"middles\":[{\"mname\":\"1\",\"bottoms\":[{\"bname\":\"x\"},{\"bname\":\"y\"}]}]}]}",
        "a1.A1x.A1y."){
        public String render(Map<String, Object> o) {
            var m = new ListContexts();
            m.putAll(o);
            var r = ListContextsRenderer.of(m);
            return r.renderString();
        }
    },
    DEEPLY_NESTED_CONTEXTS(
        DeeplyNestedContexts.class,
        "Deeply Nested Contexts",
        "{\"a\":{\"one\":1},\"b\":{\"two\":2},\"c\":{\"three\":3,\"d\":{\"four\":4,\"five\":5}}}",
        "1\n121\n12321\n1234321\n123454321\n12345654321\n123454321\n1234321\n12321\n121\n1\n"){
        public String render(Map<String, Object> o) {
            var m = new DeeplyNestedContexts();
            m.putAll(o);
            var r = DeeplyNestedContextsRenderer.of(m);
            return r.renderString();
        }
    },
    LIST(
        List.class,
        "List",
        "{\"list\":[{\"item\":1},{\"item\":2},{\"item\":3}]}",
        "\"123\""){
        public String render(Map<String, Object> o) {
            var m = new List();
            m.putAll(o);
            var r = ListRenderer.of(m);
            return r.renderString();
        }
    },
    EMPTY_LIST(
        EmptyList.class,
        "Empty List",
        "{\"list\":[]}",
        "\"\""){
        public String render(Map<String, Object> o) {
            var m = new EmptyList();
            m.putAll(o);
            var r = EmptyListRenderer.of(m);
            return r.renderString();
        }
    },
    DOUBLED(
        Doubled.class,
        "Doubled",
        "{\"bool\":true,\"two\":\"second\"}",
        "* first\n* second\n* third\n"){
        public String render(Map<String, Object> o) {
            var m = new Doubled();
            m.putAll(o);
            var r = DoubledRenderer.of(m);
            return r.renderString();
        }
    },
    NESTED__TRUTHY_(
        NestedTruthy.class,
        "Nested (Truthy)",
        "{\"bool\":true}",
        "| A B C D E |"){
        public String render(Map<String, Object> o) {
            var m = new NestedTruthy();
            m.putAll(o);
            var r = NestedTruthyRenderer.of(m);
            return r.renderString();
        }
    },
    NESTED__FALSEY_(
        NestedFalsey.class,
        "Nested (Falsey)",
        "{\"bool\":false}",
        "| A  E |"){
        public String render(Map<String, Object> o) {
            var m = new NestedFalsey();
            m.putAll(o);
            var r = NestedFalseyRenderer.of(m);
            return r.renderString();
        }
    },
    CONTEXT_MISSES(
        ContextMisses.class,
        "Context Misses",
        "{}",
        "[]"){
        public String render(Map<String, Object> o) {
            var m = new ContextMisses();
            m.putAll(o);
            var r = ContextMissesRenderer.of(m);
            return r.renderString();
        }
    },
    IMPLICIT_ITERATOR___STRING(
        ImplicitIteratorString.class,
        "Implicit Iterator - String",
        "{\"list\":[\"a\",\"b\",\"c\",\"d\",\"e\"]}",
        "\"(a)(b)(c)(d)(e)\""){
        public String render(Map<String, Object> o) {
            var m = new ImplicitIteratorString();
            m.putAll(o);
            var r = ImplicitIteratorStringRenderer.of(m);
            return r.renderString();
        }
    },
    IMPLICIT_ITERATOR___INTEGER(
        ImplicitIteratorInteger.class,
        "Implicit Iterator - Integer",
        "{\"list\":[1,2,3,4,5]}",
        "\"(1)(2)(3)(4)(5)\""){
        public String render(Map<String, Object> o) {
            var m = new ImplicitIteratorInteger();
            m.putAll(o);
            var r = ImplicitIteratorIntegerRenderer.of(m);
            return r.renderString();
        }
    },
    IMPLICIT_ITERATOR___DECIMAL(
        ImplicitIteratorDecimal.class,
        "Implicit Iterator - Decimal",
        "{\"list\":[1.1,2.2,3.3,4.4,5.5]}",
        "\"(1.1)(2.2)(3.3)(4.4)(5.5)\""){
        public String render(Map<String, Object> o) {
            var m = new ImplicitIteratorDecimal();
            m.putAll(o);
            var r = ImplicitIteratorDecimalRenderer.of(m);
            return r.renderString();
        }
    },
    IMPLICIT_ITERATOR___ARRAY(
        ImplicitIteratorArray.class,
        "Implicit Iterator - Array",
        "{\"list\":[[1,2,3],[\"a\",\"b\",\"c\"]]}",
        "\"(123)(abc)\""){
        public String render(Map<String, Object> o) {
            var m = new ImplicitIteratorArray();
            m.putAll(o);
            var r = ImplicitIteratorArrayRenderer.of(m);
            return r.renderString();
        }
    },
    DOTTED_NAMES___TRUTHY(
        DottedNamesTruthy.class,
        "Dotted Names - Truthy",
        "{\"a\":{\"b\":{\"c\":true}}}",
        "\"Here\" == \"Here\""){
        public String render(Map<String, Object> o) {
            var m = new DottedNamesTruthy();
            m.putAll(o);
            var r = DottedNamesTruthyRenderer.of(m);
            return r.renderString();
        }
    },
    DOTTED_NAMES___FALSEY(
        DottedNamesFalsey.class,
        "Dotted Names - Falsey",
        "{\"a\":{\"b\":{\"c\":false}}}",
        "\"\" == \"\""){
        public String render(Map<String, Object> o) {
            var m = new DottedNamesFalsey();
            m.putAll(o);
            var r = DottedNamesFalseyRenderer.of(m);
            return r.renderString();
        }
    },
    DOTTED_NAMES___BROKEN_CHAINS(
        DottedNamesBrokenChains.class,
        "Dotted Names - Broken Chains",
        "{\"a\":{}}",
        "\"\" == \"\""){
        public String render(Map<String, Object> o) {
            var m = new DottedNamesBrokenChains();
            m.putAll(o);
            var r = DottedNamesBrokenChainsRenderer.of(m);
            return r.renderString();
        }
    },
    SURROUNDING_WHITESPACE(
        SurroundingWhitespace.class,
        "Surrounding Whitespace",
        "{\"boolean\":true}",
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
        "Internal Whitespace",
        "{\"boolean\":true}",
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
        "Indented Inline Sections",
        "{\"boolean\":true}",
        " YES\n GOOD\n"){
        public String render(Map<String, Object> o) {
            var m = new IndentedInlineSections();
            m.putAll(o);
            var r = IndentedInlineSectionsRenderer.of(m);
            return r.renderString();
        }
    },
    STANDALONE_LINES(
        StandaloneLines.class,
        "Standalone Lines",
        "{\"boolean\":true}",
        "| This Is\n|\n| A Line\n"){
        public String render(Map<String, Object> o) {
            var m = new StandaloneLines();
            m.putAll(o);
            var r = StandaloneLinesRenderer.of(m);
            return r.renderString();
        }
    },
    INDENTED_STANDALONE_LINES(
        IndentedStandaloneLines.class,
        "Indented Standalone Lines",
        "{\"boolean\":true}",
        "| This Is\n|\n| A Line\n"){
        public String render(Map<String, Object> o) {
            var m = new IndentedStandaloneLines();
            m.putAll(o);
            var r = IndentedStandaloneLinesRenderer.of(m);
            return r.renderString();
        }
    },
    STANDALONE_LINE_ENDINGS(
        StandaloneLineEndings.class,
        "Standalone Line Endings",
        "{\"boolean\":true}",
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
        "Standalone Without Previous Line",
        "{\"boolean\":true}",
        "#\n/"){
        public String render(Map<String, Object> o) {
            var m = new StandaloneWithoutPreviousLine();
            m.putAll(o);
            var r = StandaloneWithoutPreviousLineRenderer.of(m);
            return r.renderString();
        }
    },
    STANDALONE_WITHOUT_NEWLINE(
        StandaloneWithoutNewline.class,
        "Standalone Without Newline",
        "{\"boolean\":true}",
        "#\n/\n"){
        public String render(Map<String, Object> o) {
            var m = new StandaloneWithoutNewline();
            m.putAll(o);
            var r = StandaloneWithoutNewlineRenderer.of(m);
            return r.renderString();
        }
    },
    PADDING(
        Padding.class,
        "Padding",
        "{\"boolean\":true}",
        "|=|"){
        public String render(Map<String, Object> o) {
            var m = new Padding();
            m.putAll(o);
            var r = PaddingRenderer.of(m);
            return r.renderString();
        }
    },
    ;
    private final Class<?> templateClass;
    private final String json;
    private final String title;
    private final String expected;

    private SectionsSpecTemplate(
        Class<?> templateClass,
        String title,
        String json,
        String expected) {
        this.templateClass = templateClass;
        this.title = title;
        this.json = json;
        this.expected = expected;
    }
    public Class<?> templateClass() {
        return templateClass;
    }
    public String title() {
        return this.title;
    }
    public String json() {
        return this.json;
    }
    public String expected() {
        return this.expected;
    }
    public abstract String render(Map<String, Object> o);

    public static final String TRUTHY_FILE = "sections/Truthy.mustache";
    public static final String FALSEY_FILE = "sections/Falsey.mustache";
    public static final String NULL_IS_FALSEY_FILE = "sections/Nullisfalsey.mustache";
    public static final String CONTEXT_FILE = "sections/Context.mustache";
    public static final String PARENT_CONTEXTS_FILE = "sections/Parentcontexts.mustache";
    public static final String VARIABLE_TEST_FILE = "sections/Variabletest.mustache";
    public static final String LIST_CONTEXTS_FILE = "sections/ListContexts.mustache";
    public static final String DEEPLY_NESTED_CONTEXTS_FILE = "sections/DeeplyNestedContexts.mustache";
    public static final String LIST_FILE = "sections/List.mustache";
    public static final String EMPTY_LIST_FILE = "sections/EmptyList.mustache";
    public static final String DOUBLED_FILE = "sections/Doubled.mustache";
    public static final String NESTED__TRUTHY__FILE = "sections/NestedTruthy.mustache";
    public static final String NESTED__FALSEY__FILE = "sections/NestedFalsey.mustache";
    public static final String CONTEXT_MISSES_FILE = "sections/ContextMisses.mustache";
    public static final String IMPLICIT_ITERATOR___STRING_FILE = "sections/ImplicitIteratorString.mustache";
    public static final String IMPLICIT_ITERATOR___INTEGER_FILE = "sections/ImplicitIteratorInteger.mustache";
    public static final String IMPLICIT_ITERATOR___DECIMAL_FILE = "sections/ImplicitIteratorDecimal.mustache";
    public static final String IMPLICIT_ITERATOR___ARRAY_FILE = "sections/ImplicitIteratorArray.mustache";
    public static final String DOTTED_NAMES___TRUTHY_FILE = "sections/DottedNamesTruthy.mustache";
    public static final String DOTTED_NAMES___FALSEY_FILE = "sections/DottedNamesFalsey.mustache";
    public static final String DOTTED_NAMES___BROKEN_CHAINS_FILE = "sections/DottedNamesBrokenChains.mustache";
    public static final String SURROUNDING_WHITESPACE_FILE = "sections/SurroundingWhitespace.mustache";
    public static final String INTERNAL_WHITESPACE_FILE = "sections/InternalWhitespace.mustache";
    public static final String INDENTED_INLINE_SECTIONS_FILE = "sections/IndentedInlineSections.mustache";
    public static final String STANDALONE_LINES_FILE = "sections/StandaloneLines.mustache";
    public static final String INDENTED_STANDALONE_LINES_FILE = "sections/IndentedStandaloneLines.mustache";
    public static final String STANDALONE_LINE_ENDINGS_FILE = "sections/StandaloneLineEndings.mustache";
    public static final String STANDALONE_WITHOUT_PREVIOUS_LINE_FILE = "sections/StandaloneWithoutPreviousLine.mustache";
    public static final String STANDALONE_WITHOUT_NEWLINE_FILE = "sections/StandaloneWithoutNewline.mustache";
    public static final String PADDING_FILE = "sections/Padding.mustache";
}
