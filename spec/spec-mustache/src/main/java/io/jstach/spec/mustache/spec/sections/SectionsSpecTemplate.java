package io.jstach.spec.mustache.spec.sections;

import java.util.Map;

import io.jstach.spec.generator.SpecListing;

public enum SectionsSpecTemplate implements SpecListing {
    TRUTHY(
        Truthy.class,
        "sections",
        "Truthy",
        "Truthy sections should have their contents rendered.",
        "{\"boolean\":true}",
        "\"{{#boolean}}This should be rendered.{{/boolean}}\"",
        "\"This should be rendered.\"",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new Truthy();
            m.putAll(o);
            var r = TruthyRenderer.of(m);
            return r.renderString();
        }
    },
    FALSEY(
        Falsey.class,
        "sections",
        "Falsey",
        "Falsey sections should have their contents omitted.",
        "{\"boolean\":false}",
        "\"{{#boolean}}This should not be rendered.{{/boolean}}\"",
        "\"\"",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new Falsey();
            m.putAll(o);
            var r = FalseyRenderer.of(m);
            return r.renderString();
        }
    },
    NULL_IS_FALSEY(
        Nullisfalsey.class,
        "sections",
        "Null is falsey",
        "Null is falsey.",
        "{\"null\":null}",
        "\"{{#null}}This should not be rendered.{{/null}}\"",
        "\"\"",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new Nullisfalsey();
            m.putAll(o);
            var r = NullisfalseyRenderer.of(m);
            return r.renderString();
        }
    },
    CONTEXT(
        Context.class,
        "sections",
        "Context",
        "Objects and hashes should be pushed onto the context stack.",
        "{\"context\":{\"name\":\"Joe\"}}",
        "\"{{#context}}Hi {{name}}.{{/context}}\"",
        "\"Hi Joe.\"",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new Context();
            m.putAll(o);
            var r = ContextRenderer.of(m);
            return r.renderString();
        }
    },
    PARENT_CONTEXTS(
        Parentcontexts.class,
        "sections",
        "Parent contexts",
        "Names missing in the current context are looked up in the stack.",
        "{\"a\":\"foo\",\"b\":\"wrong\",\"sec\":{\"b\":\"bar\"},\"c\":{\"d\":\"baz\"}}",
        "\"{{#sec}}{{a}}, {{b}}, {{c.d}}{{/sec}}\"",
        "\"foo, bar, baz\"",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new Parentcontexts();
            m.putAll(o);
            var r = ParentcontextsRenderer.of(m);
            return r.renderString();
        }
    },
    VARIABLE_TEST(
        Variabletest.class,
        "sections",
        "Variable test",
        "Non-false sections have their value at the top of context,\naccessible as {{.}} or through the parent context. This gives\na simple way to display content conditionally if a variable exists.\n",
        "{\"foo\":\"bar\"}",
        "\"{{#foo}}{{.}} is {{foo}}{{/foo}}\"",
        "\"bar is bar\"",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new Variabletest();
            m.putAll(o);
            var r = VariabletestRenderer.of(m);
            return r.renderString();
        }
    },
    LIST_CONTEXTS(
        ListContexts.class,
        "sections",
        "List Contexts",
        "All elements on the context stack should be accessible within lists.",
        "{\"tops\":[{\"tname\":{\"upper\":\"A\",\"lower\":\"a\"},\"middles\":[{\"mname\":\"1\",\"bottoms\":[{\"bname\":\"x\"},{\"bname\":\"y\"}]}]}]}",
        "{{#tops}}{{#middles}}{{tname.lower}}{{mname}}.{{#bottoms}}{{tname.upper}}{{mname}}{{bname}}.{{/bottoms}}{{/middles}}{{/tops}}",
        "a1.A1x.A1y.",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new ListContexts();
            m.putAll(o);
            var r = ListContextsRenderer.of(m);
            return r.renderString();
        }
    },
    DEEPLY_NESTED_CONTEXTS(
        DeeplyNestedContexts.class,
        "sections",
        "Deeply Nested Contexts",
        "All elements on the context stack should be accessible.",
        "{\"a\":{\"one\":1},\"b\":{\"two\":2},\"c\":{\"three\":3,\"d\":{\"four\":4,\"five\":5}}}",
        "{{#a}}\n{{one}}\n{{#b}}\n{{one}}{{two}}{{one}}\n{{#c}}\n{{one}}{{two}}{{three}}{{two}}{{one}}\n{{#d}}\n{{one}}{{two}}{{three}}{{four}}{{three}}{{two}}{{one}}\n{{#five}}\n{{one}}{{two}}{{three}}{{four}}{{five}}{{four}}{{three}}{{two}}{{one}}\n{{one}}{{two}}{{three}}{{four}}{{.}}6{{.}}{{four}}{{three}}{{two}}{{one}}\n{{one}}{{two}}{{three}}{{four}}{{five}}{{four}}{{three}}{{two}}{{one}}\n{{/five}}\n{{one}}{{two}}{{three}}{{four}}{{three}}{{two}}{{one}}\n{{/d}}\n{{one}}{{two}}{{three}}{{two}}{{one}}\n{{/c}}\n{{one}}{{two}}{{one}}\n{{/b}}\n{{one}}\n{{/a}}\n",
        "1\n121\n12321\n1234321\n123454321\n12345654321\n123454321\n1234321\n12321\n121\n1\n",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new DeeplyNestedContexts();
            m.putAll(o);
            var r = DeeplyNestedContextsRenderer.of(m);
            return r.renderString();
        }
    },
    LIST(
        List.class,
        "sections",
        "List",
        "Lists should be iterated; list items should visit the context stack.",
        "{\"list\":[{\"item\":1},{\"item\":2},{\"item\":3}]}",
        "\"{{#list}}{{item}}{{/list}}\"",
        "\"123\"",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new List();
            m.putAll(o);
            var r = ListRenderer.of(m);
            return r.renderString();
        }
    },
    EMPTY_LIST(
        EmptyList.class,
        "sections",
        "Empty List",
        "Empty lists should behave like falsey values.",
        "{\"list\":[]}",
        "\"{{#list}}Yay lists!{{/list}}\"",
        "\"\"",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new EmptyList();
            m.putAll(o);
            var r = EmptyListRenderer.of(m);
            return r.renderString();
        }
    },
    DOUBLED(
        Doubled.class,
        "sections",
        "Doubled",
        "Multiple sections per template should be permitted.",
        "{\"bool\":true,\"two\":\"second\"}",
        "{{#bool}}\n* first\n{{/bool}}\n* {{two}}\n{{#bool}}\n* third\n{{/bool}}\n",
        "* first\n* second\n* third\n",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new Doubled();
            m.putAll(o);
            var r = DoubledRenderer.of(m);
            return r.renderString();
        }
    },
    NESTED__TRUTHY_(
        NestedTruthy.class,
        "sections",
        "Nested (Truthy)",
        "Nested truthy sections should have their contents rendered.",
        "{\"bool\":true}",
        "| A {{#bool}}B {{#bool}}C{{/bool}} D{{/bool}} E |",
        "| A B C D E |",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new NestedTruthy();
            m.putAll(o);
            var r = NestedTruthyRenderer.of(m);
            return r.renderString();
        }
    },
    NESTED__FALSEY_(
        NestedFalsey.class,
        "sections",
        "Nested (Falsey)",
        "Nested falsey sections should be omitted.",
        "{\"bool\":false}",
        "| A {{#bool}}B {{#bool}}C{{/bool}} D{{/bool}} E |",
        "| A  E |",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new NestedFalsey();
            m.putAll(o);
            var r = NestedFalseyRenderer.of(m);
            return r.renderString();
        }
    },
    CONTEXT_MISSES(
        ContextMisses.class,
        "sections",
        "Context Misses",
        "Failed context lookups should be considered falsey.",
        "{}",
        "[{{#missing}}Found key 'missing'!{{/missing}}]",
        "[]",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new ContextMisses();
            m.putAll(o);
            var r = ContextMissesRenderer.of(m);
            return r.renderString();
        }
    },
    IMPLICIT_ITERATOR___STRING(
        ImplicitIteratorString.class,
        "sections",
        "Implicit Iterator - String",
        "Implicit iterators should directly interpolate strings.",
        "{\"list\":[\"a\",\"b\",\"c\",\"d\",\"e\"]}",
        "\"{{#list}}({{.}}){{/list}}\"",
        "\"(a)(b)(c)(d)(e)\"",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new ImplicitIteratorString();
            m.putAll(o);
            var r = ImplicitIteratorStringRenderer.of(m);
            return r.renderString();
        }
    },
    IMPLICIT_ITERATOR___INTEGER(
        ImplicitIteratorInteger.class,
        "sections",
        "Implicit Iterator - Integer",
        "Implicit iterators should cast integers to strings and interpolate.",
        "{\"list\":[1,2,3,4,5]}",
        "\"{{#list}}({{.}}){{/list}}\"",
        "\"(1)(2)(3)(4)(5)\"",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new ImplicitIteratorInteger();
            m.putAll(o);
            var r = ImplicitIteratorIntegerRenderer.of(m);
            return r.renderString();
        }
    },
    IMPLICIT_ITERATOR___DECIMAL(
        ImplicitIteratorDecimal.class,
        "sections",
        "Implicit Iterator - Decimal",
        "Implicit iterators should cast decimals to strings and interpolate.",
        "{\"list\":[1.1,2.2,3.3,4.4,5.5]}",
        "\"{{#list}}({{.}}){{/list}}\"",
        "\"(1.1)(2.2)(3.3)(4.4)(5.5)\"",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new ImplicitIteratorDecimal();
            m.putAll(o);
            var r = ImplicitIteratorDecimalRenderer.of(m);
            return r.renderString();
        }
    },
    IMPLICIT_ITERATOR___ARRAY(
        ImplicitIteratorArray.class,
        "sections",
        "Implicit Iterator - Array",
        "Implicit iterators should allow iterating over nested arrays.",
        "{\"list\":[[1,2,3],[\"a\",\"b\",\"c\"]]}",
        "\"{{#list}}({{#.}}{{.}}{{/.}}){{/list}}\"",
        "\"(123)(abc)\"",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new ImplicitIteratorArray();
            m.putAll(o);
            var r = ImplicitIteratorArrayRenderer.of(m);
            return r.renderString();
        }
    },
    DOTTED_NAMES___TRUTHY(
        DottedNamesTruthy.class,
        "sections",
        "Dotted Names - Truthy",
        "Dotted names should be valid for Section tags.",
        "{\"a\":{\"b\":{\"c\":true}}}",
        "\"{{#a.b.c}}Here{{/a.b.c}}\" == \"Here\"",
        "\"Here\" == \"Here\"",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new DottedNamesTruthy();
            m.putAll(o);
            var r = DottedNamesTruthyRenderer.of(m);
            return r.renderString();
        }
    },
    DOTTED_NAMES___FALSEY(
        DottedNamesFalsey.class,
        "sections",
        "Dotted Names - Falsey",
        "Dotted names should be valid for Section tags.",
        "{\"a\":{\"b\":{\"c\":false}}}",
        "\"{{#a.b.c}}Here{{/a.b.c}}\" == \"\"",
        "\"\" == \"\"",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new DottedNamesFalsey();
            m.putAll(o);
            var r = DottedNamesFalseyRenderer.of(m);
            return r.renderString();
        }
    },
    DOTTED_NAMES___BROKEN_CHAINS(
        DottedNamesBrokenChains.class,
        "sections",
        "Dotted Names - Broken Chains",
        "Dotted names that cannot be resolved should be considered falsey.",
        "{\"a\":{}}",
        "\"{{#a.b.c}}Here{{/a.b.c}}\" == \"\"",
        "\"\" == \"\"",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new DottedNamesBrokenChains();
            m.putAll(o);
            var r = DottedNamesBrokenChainsRenderer.of(m);
            return r.renderString();
        }
    },
    SURROUNDING_WHITESPACE(
        SurroundingWhitespace.class,
        "sections",
        "Surrounding Whitespace",
        "Sections should not alter surrounding whitespace.",
        "{\"boolean\":true}",
        " | {{#boolean}}\t|\t{{/boolean}} | \n",
        " | \t|\t | \n",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new SurroundingWhitespace();
            m.putAll(o);
            var r = SurroundingWhitespaceRenderer.of(m);
            return r.renderString();
        }
    },
    INTERNAL_WHITESPACE(
        InternalWhitespace.class,
        "sections",
        "Internal Whitespace",
        "Sections should not alter internal whitespace.",
        "{\"boolean\":true}",
        " | {{#boolean}} {{! Important Whitespace }}\n {{/boolean}} | \n",
        " |  \n  | \n",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new InternalWhitespace();
            m.putAll(o);
            var r = InternalWhitespaceRenderer.of(m);
            return r.renderString();
        }
    },
    INDENTED_INLINE_SECTIONS(
        IndentedInlineSections.class,
        "sections",
        "Indented Inline Sections",
        "Single-line sections should not alter surrounding whitespace.",
        "{\"boolean\":true}",
        " {{#boolean}}YES{{/boolean}}\n {{#boolean}}GOOD{{/boolean}}\n",
        " YES\n GOOD\n",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new IndentedInlineSections();
            m.putAll(o);
            var r = IndentedInlineSectionsRenderer.of(m);
            return r.renderString();
        }
    },
    STANDALONE_LINES(
        StandaloneLines.class,
        "sections",
        "Standalone Lines",
        "Standalone lines should be removed from the template.",
        "{\"boolean\":true}",
        "| This Is\n{{#boolean}}\n|\n{{/boolean}}\n| A Line\n",
        "| This Is\n|\n| A Line\n",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new StandaloneLines();
            m.putAll(o);
            var r = StandaloneLinesRenderer.of(m);
            return r.renderString();
        }
    },
    INDENTED_STANDALONE_LINES(
        IndentedStandaloneLines.class,
        "sections",
        "Indented Standalone Lines",
        "Indented standalone lines should be removed from the template.",
        "{\"boolean\":true}",
        "| This Is\n  {{#boolean}}\n|\n  {{/boolean}}\n| A Line\n",
        "| This Is\n|\n| A Line\n",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new IndentedStandaloneLines();
            m.putAll(o);
            var r = IndentedStandaloneLinesRenderer.of(m);
            return r.renderString();
        }
    },
    STANDALONE_LINE_ENDINGS(
        StandaloneLineEndings.class,
        "sections",
        "Standalone Line Endings",
        "\"\\r\\n\" should be considered a newline for standalone tags.",
        "{\"boolean\":true}",
        "|\r\n{{#boolean}}\r\n{{/boolean}}\r\n|",
        "|\r\n|",
        Map.of()
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
        "sections",
        "Standalone Without Previous Line",
        "Standalone tags should not require a newline to precede them.",
        "{\"boolean\":true}",
        "  {{#boolean}}\n#{{/boolean}}\n/",
        "#\n/",
        Map.of()
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
        "sections",
        "Standalone Without Newline",
        "Standalone tags should not require a newline to follow them.",
        "{\"boolean\":true}",
        "#{{#boolean}}\n/\n  {{/boolean}}",
        "#\n/\n",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new StandaloneWithoutNewline();
            m.putAll(o);
            var r = StandaloneWithoutNewlineRenderer.of(m);
            return r.renderString();
        }
    },
    PADDING(
        Padding.class,
        "sections",
        "Padding",
        "Superfluous in-tag whitespace should be ignored.",
        "{\"boolean\":true}",
        "|{{# boolean }}={{/ boolean }}|",
        "|=|",
        Map.of()
        ){
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
    private final Map<String,String> partials;

    private SectionsSpecTemplate(
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
