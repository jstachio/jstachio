package com.snaphop.staticmustache.spec.interpolation;

import com.snaphop.staticmustache.spec.SpecListing;
import java.util.Map;

public enum InterpolationSpecTemplate implements SpecListing {
    NO_INTERPOLATION(
        NoInterpolation.class,
        "No Interpolation",
        "{}",
        "Hello from {Mustache}!\n"){
        public String render(Map<String, Object> o) {
            var m = new NoInterpolation();
            m.putAll(o);
            var r = NoInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    BASIC_INTERPOLATION(
        BasicInterpolation.class,
        "Basic Interpolation",
        "{\"subject\":\"world\"}",
        "Hello, world!\n"){
        public String render(Map<String, Object> o) {
            var m = new BasicInterpolation();
            m.putAll(o);
            var r = BasicInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    HTML_ESCAPING(
        HTMLEscaping.class,
        "HTML Escaping",
        "{\"forbidden\":\"& \\\" < >\"}",
        "These characters should be HTML escaped: &amp; &quot; &lt; &gt;\n"){
        public String render(Map<String, Object> o) {
            var m = new HTMLEscaping();
            m.putAll(o);
            var r = HTMLEscapingRenderer.of(m);
            return r.renderString();
        }
    },
    TRIPLE_MUSTACHE(
        TripleMustache.class,
        "Triple Mustache",
        "{\"forbidden\":\"& \\\" < >\"}",
        "These characters should not be HTML escaped: & \" < >\n"){
        public String render(Map<String, Object> o) {
            var m = new TripleMustache();
            m.putAll(o);
            var r = TripleMustacheRenderer.of(m);
            return r.renderString();
        }
    },
    AMPERSAND(
        Ampersand.class,
        "Ampersand",
        "{\"forbidden\":\"& \\\" < >\"}",
        "These characters should not be HTML escaped: & \" < >\n"){
        public String render(Map<String, Object> o) {
            var m = new Ampersand();
            m.putAll(o);
            var r = AmpersandRenderer.of(m);
            return r.renderString();
        }
    },
    BASIC_INTEGER_INTERPOLATION(
        BasicIntegerInterpolation.class,
        "Basic Integer Interpolation",
        "{\"mph\":85}",
        "\"85 miles an hour!\""){
        public String render(Map<String, Object> o) {
            var m = new BasicIntegerInterpolation();
            m.putAll(o);
            var r = BasicIntegerInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    TRIPLE_MUSTACHE_INTEGER_INTERPOLATION(
        TripleMustacheIntegerInterpolation.class,
        "Triple Mustache Integer Interpolation",
        "{\"mph\":85}",
        "\"85 miles an hour!\""){
        public String render(Map<String, Object> o) {
            var m = new TripleMustacheIntegerInterpolation();
            m.putAll(o);
            var r = TripleMustacheIntegerInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    AMPERSAND_INTEGER_INTERPOLATION(
        AmpersandIntegerInterpolation.class,
        "Ampersand Integer Interpolation",
        "{\"mph\":85}",
        "\"85 miles an hour!\""){
        public String render(Map<String, Object> o) {
            var m = new AmpersandIntegerInterpolation();
            m.putAll(o);
            var r = AmpersandIntegerInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    BASIC_DECIMAL_INTERPOLATION(
        BasicDecimalInterpolation.class,
        "Basic Decimal Interpolation",
        "{\"power\":1.21}",
        "\"1.21 jiggawatts!\""){
        public String render(Map<String, Object> o) {
            var m = new BasicDecimalInterpolation();
            m.putAll(o);
            var r = BasicDecimalInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    TRIPLE_MUSTACHE_DECIMAL_INTERPOLATION(
        TripleMustacheDecimalInterpolation.class,
        "Triple Mustache Decimal Interpolation",
        "{\"power\":1.21}",
        "\"1.21 jiggawatts!\""){
        public String render(Map<String, Object> o) {
            var m = new TripleMustacheDecimalInterpolation();
            m.putAll(o);
            var r = TripleMustacheDecimalInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    AMPERSAND_DECIMAL_INTERPOLATION(
        AmpersandDecimalInterpolation.class,
        "Ampersand Decimal Interpolation",
        "{\"power\":1.21}",
        "\"1.21 jiggawatts!\""){
        public String render(Map<String, Object> o) {
            var m = new AmpersandDecimalInterpolation();
            m.putAll(o);
            var r = AmpersandDecimalInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    BASIC_NULL_INTERPOLATION(
        BasicNullInterpolation.class,
        "Basic Null Interpolation",
        "{\"cannot\":null}",
        "I () be seen!"){
        public String render(Map<String, Object> o) {
            var m = new BasicNullInterpolation();
            m.putAll(o);
            var r = BasicNullInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    TRIPLE_MUSTACHE_NULL_INTERPOLATION(
        TripleMustacheNullInterpolation.class,
        "Triple Mustache Null Interpolation",
        "{\"cannot\":null}",
        "I () be seen!"){
        public String render(Map<String, Object> o) {
            var m = new TripleMustacheNullInterpolation();
            m.putAll(o);
            var r = TripleMustacheNullInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    AMPERSAND_NULL_INTERPOLATION(
        AmpersandNullInterpolation.class,
        "Ampersand Null Interpolation",
        "{\"cannot\":null}",
        "I () be seen!"){
        public String render(Map<String, Object> o) {
            var m = new AmpersandNullInterpolation();
            m.putAll(o);
            var r = AmpersandNullInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    BASIC_CONTEXT_MISS_INTERPOLATION(
        BasicContextMissInterpolation.class,
        "Basic Context Miss Interpolation",
        "{}",
        "I () be seen!"){
        public String render(Map<String, Object> o) {
            var m = new BasicContextMissInterpolation();
            m.putAll(o);
            var r = BasicContextMissInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    TRIPLE_MUSTACHE_CONTEXT_MISS_INTERPOLATION(
        TripleMustacheContextMissInterpolation.class,
        "Triple Mustache Context Miss Interpolation",
        "{}",
        "I () be seen!"){
        public String render(Map<String, Object> o) {
            var m = new TripleMustacheContextMissInterpolation();
            m.putAll(o);
            var r = TripleMustacheContextMissInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    AMPERSAND_CONTEXT_MISS_INTERPOLATION(
        AmpersandContextMissInterpolation.class,
        "Ampersand Context Miss Interpolation",
        "{}",
        "I () be seen!"){
        public String render(Map<String, Object> o) {
            var m = new AmpersandContextMissInterpolation();
            m.putAll(o);
            var r = AmpersandContextMissInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    DOTTED_NAMES___BASIC_INTERPOLATION(
        DottedNamesBasicInterpolation.class,
        "Dotted Names - Basic Interpolation",
        "{\"person\":{\"name\":\"Joe\"}}",
        "\"Joe\" == \"Joe\""){
        public String render(Map<String, Object> o) {
            var m = new DottedNamesBasicInterpolation();
            m.putAll(o);
            var r = DottedNamesBasicInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    DOTTED_NAMES___TRIPLE_MUSTACHE_INTERPOLATION(
        DottedNamesTripleMustacheInterpolation.class,
        "Dotted Names - Triple Mustache Interpolation",
        "{\"person\":{\"name\":\"Joe\"}}",
        "\"Joe\" == \"Joe\""){
        public String render(Map<String, Object> o) {
            var m = new DottedNamesTripleMustacheInterpolation();
            m.putAll(o);
            var r = DottedNamesTripleMustacheInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    DOTTED_NAMES___AMPERSAND_INTERPOLATION(
        DottedNamesAmpersandInterpolation.class,
        "Dotted Names - Ampersand Interpolation",
        "{\"person\":{\"name\":\"Joe\"}}",
        "\"Joe\" == \"Joe\""){
        public String render(Map<String, Object> o) {
            var m = new DottedNamesAmpersandInterpolation();
            m.putAll(o);
            var r = DottedNamesAmpersandInterpolationRenderer.of(m);
            return r.renderString();
        }
    },
    DOTTED_NAMES___ARBITRARY_DEPTH(
        DottedNamesArbitraryDepth.class,
        "Dotted Names - Arbitrary Depth",
        "{\"a\":{\"b\":{\"c\":{\"d\":{\"e\":{\"name\":\"Phil\"}}}}}}",
        "\"Phil\" == \"Phil\""){
        public String render(Map<String, Object> o) {
            var m = new DottedNamesArbitraryDepth();
            m.putAll(o);
            var r = DottedNamesArbitraryDepthRenderer.of(m);
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
    DOTTED_NAMES___BROKEN_CHAIN_RESOLUTION(
        DottedNamesBrokenChainResolution.class,
        "Dotted Names - Broken Chain Resolution",
        "{\"a\":{\"b\":{}},\"c\":{\"name\":\"Jim\"}}",
        "\"\" == \"\""){
        public String render(Map<String, Object> o) {
            var m = new DottedNamesBrokenChainResolution();
            m.putAll(o);
            var r = DottedNamesBrokenChainResolutionRenderer.of(m);
            return r.renderString();
        }
    },
    DOTTED_NAMES___INITIAL_RESOLUTION(
        DottedNamesInitialResolution.class,
        "Dotted Names - Initial Resolution",
        "{\"a\":{\"b\":{\"c\":{\"d\":{\"e\":{\"name\":\"Phil\"}}}}},\"b\":{\"c\":{\"d\":{\"e\":{\"name\":\"Wrong\"}}}}}",
        "\"Phil\" == \"Phil\""){
        public String render(Map<String, Object> o) {
            var m = new DottedNamesInitialResolution();
            m.putAll(o);
            var r = DottedNamesInitialResolutionRenderer.of(m);
            return r.renderString();
        }
    },
    DOTTED_NAMES___CONTEXT_PRECEDENCE(
        DottedNamesContextPrecedence.class,
        "Dotted Names - Context Precedence",
        "{\"a\":{\"b\":{}},\"b\":{\"c\":\"ERROR\"}}",
        ""){
        public String render(Map<String, Object> o) {
            var m = new DottedNamesContextPrecedence();
            m.putAll(o);
            var r = DottedNamesContextPrecedenceRenderer.of(m);
            return r.renderString();
        }
    },
    INTERPOLATION___SURROUNDING_WHITESPACE(
        InterpolationSurroundingWhitespace.class,
        "Interpolation - Surrounding Whitespace",
        "{\"string\":\"---\"}",
        "| --- |"){
        public String render(Map<String, Object> o) {
            var m = new InterpolationSurroundingWhitespace();
            m.putAll(o);
            var r = InterpolationSurroundingWhitespaceRenderer.of(m);
            return r.renderString();
        }
    },
    TRIPLE_MUSTACHE___SURROUNDING_WHITESPACE(
        TripleMustacheSurroundingWhitespace.class,
        "Triple Mustache - Surrounding Whitespace",
        "{\"string\":\"---\"}",
        "| --- |"){
        public String render(Map<String, Object> o) {
            var m = new TripleMustacheSurroundingWhitespace();
            m.putAll(o);
            var r = TripleMustacheSurroundingWhitespaceRenderer.of(m);
            return r.renderString();
        }
    },
    AMPERSAND___SURROUNDING_WHITESPACE(
        AmpersandSurroundingWhitespace.class,
        "Ampersand - Surrounding Whitespace",
        "{\"string\":\"---\"}",
        "| --- |"){
        public String render(Map<String, Object> o) {
            var m = new AmpersandSurroundingWhitespace();
            m.putAll(o);
            var r = AmpersandSurroundingWhitespaceRenderer.of(m);
            return r.renderString();
        }
    },
    INTERPOLATION___STANDALONE(
        InterpolationStandalone.class,
        "Interpolation - Standalone",
        "{\"string\":\"---\"}",
        "  ---\n"){
        public String render(Map<String, Object> o) {
            var m = new InterpolationStandalone();
            m.putAll(o);
            var r = InterpolationStandaloneRenderer.of(m);
            return r.renderString();
        }
    },
    TRIPLE_MUSTACHE___STANDALONE(
        TripleMustacheStandalone.class,
        "Triple Mustache - Standalone",
        "{\"string\":\"---\"}",
        "  ---\n"){
        public String render(Map<String, Object> o) {
            var m = new TripleMustacheStandalone();
            m.putAll(o);
            var r = TripleMustacheStandaloneRenderer.of(m);
            return r.renderString();
        }
    },
    AMPERSAND___STANDALONE(
        AmpersandStandalone.class,
        "Ampersand - Standalone",
        "{\"string\":\"---\"}",
        "  ---\n"){
        public String render(Map<String, Object> o) {
            var m = new AmpersandStandalone();
            m.putAll(o);
            var r = AmpersandStandaloneRenderer.of(m);
            return r.renderString();
        }
    },
    INTERPOLATION_WITH_PADDING(
        InterpolationWithPadding.class,
        "Interpolation With Padding",
        "{\"string\":\"---\"}",
        "|---|"){
        public String render(Map<String, Object> o) {
            var m = new InterpolationWithPadding();
            m.putAll(o);
            var r = InterpolationWithPaddingRenderer.of(m);
            return r.renderString();
        }
    },
    TRIPLE_MUSTACHE_WITH_PADDING(
        TripleMustacheWithPadding.class,
        "Triple Mustache With Padding",
        "{\"string\":\"---\"}",
        "|---|"){
        public String render(Map<String, Object> o) {
            var m = new TripleMustacheWithPadding();
            m.putAll(o);
            var r = TripleMustacheWithPaddingRenderer.of(m);
            return r.renderString();
        }
    },
    AMPERSAND_WITH_PADDING(
        AmpersandWithPadding.class,
        "Ampersand With Padding",
        "{\"string\":\"---\"}",
        "|---|"){
        public String render(Map<String, Object> o) {
            var m = new AmpersandWithPadding();
            m.putAll(o);
            var r = AmpersandWithPaddingRenderer.of(m);
            return r.renderString();
        }
    },
    ;
    private final Class<?> templateClass;
    private final String json;
    private final String title;
    private final String expected;

    private InterpolationSpecTemplate(
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
