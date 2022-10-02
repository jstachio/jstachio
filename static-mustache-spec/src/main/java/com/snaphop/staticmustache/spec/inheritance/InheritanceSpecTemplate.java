package com.snaphop.staticmustache.spec.inheritance;

import com.snaphop.staticmustache.spec.SpecListing;
import java.util.Map;

public enum InheritanceSpecTemplate implements SpecListing {
    DEFAULT(
        null,
        "inheritance",
        "Default",
        "Default content should be rendered if the block isn't overridden",
        "{}",
        "{{$title}}Default title{{/title}}\n",
        "Default title\n"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    VARIABLE(
        null,
        "inheritance",
        "Variable",
        "Default content renders variables",
        "{\"bar\":\"baz\"}",
        "{{$foo}}default {{bar}} content{{/foo}}\n",
        "default baz content\n"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    TRIPLE_MUSTACHE(
        null,
        "inheritance",
        "Triple Mustache",
        "Default content renders triple mustache variables",
        "{\"bar\":\"<baz>\"}",
        "{{$foo}}default {{{bar}}} content{{/foo}}\n",
        "default <baz> content\n"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    SECTIONS(
        null,
        "inheritance",
        "Sections",
        "Default content renders sections",
        "{\"bar\":{\"baz\":\"qux\"}}",
        "{{$foo}}default {{#bar}}{{baz}}{{/bar}} content{{/foo}}\n",
        "default qux content\n"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    NEGATIVE_SECTIONS(
        null,
        "inheritance",
        "Negative Sections",
        "Default content renders negative sections",
        "{\"baz\":\"three\"}",
        "{{$foo}}default {{^bar}}{{baz}}{{/bar}} content{{/foo}}\n",
        "default three content\n"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    MUSTACHE_INJECTION(
        null,
        "inheritance",
        "Mustache Injection",
        "Mustache injection in default content",
        "{\"bar\":{\"baz\":\"{{qux}}\"}}",
        "{{$foo}}default {{#bar}}{{baz}}{{/bar}} content{{/foo}}\n",
        "default {{qux}} content\n"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    INHERIT(
        null,
        "inheritance",
        "Inherit",
        "Default content rendered inside inherited templates",
        "{}",
        "{{<include}}{{/include}}\n",
        "default content"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    OVERRIDDEN_CONTENT(
        null,
        "inheritance",
        "Overridden content",
        "Overridden content",
        "{}",
        "{{<super}}{{$title}}sub template title{{/title}}{{/super}}",
        "...sub template title..."){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    DATA_DOES_NOT_OVERRIDE_BLOCK(
        null,
        "inheritance",
        "Data does not override block",
        "Context does not override argument passed into parent",
        "{\"var\":\"var in data\"}",
        "{{<include}}{{$var}}var in template{{/var}}{{/include}}",
        "var in template"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    DATA_DOES_NOT_OVERRIDE_BLOCK_DEFAULT(
        null,
        "inheritance",
        "Data does not override block default",
        "Context does not override default content of block",
        "{\"var\":\"var in data\"}",
        "{{<include}}{{/include}}",
        "var in include"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    OVERRIDDEN_PARENT(
        null,
        "inheritance",
        "Overridden parent",
        "Overridden parent",
        "{}",
        "test {{<parent}}{{$stuff}}override{{/stuff}}{{/parent}}",
        "test override"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    TWO_OVERRIDDEN_PARENTS(
        null,
        "inheritance",
        "Two overridden parents",
        "Two overridden parents with different content",
        "{}",
        "test {{<parent}}{{$stuff}}override1{{/stuff}}{{/parent}} {{<parent}}{{$stuff}}override2{{/stuff}}{{/parent}}\n",
        "test |override1 default| |override2 default|\n"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    OVERRIDE_PARENT_WITH_NEWLINES(
        null,
        "inheritance",
        "Override parent with newlines",
        "Override parent with newlines",
        "{}",
        "{{<parent}}{{$ballmer}}\npeaked\n\n:(\n{{/ballmer}}{{/parent}}",
        "peaked\n\n:(\n"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    INHERIT_INDENTATION(
        null,
        "inheritance",
        "Inherit indentation",
        "Inherit indentation when overriding a parent",
        "{}",
        "{{<parent}}{{$nineties}}hammer time{{/nineties}}{{/parent}}",
        "stop:\n  hammer time\n"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    ONLY_ONE_OVERRIDE(
        null,
        "inheritance",
        "Only one override",
        "Override one parameter but not the other",
        "{}",
        "{{<parent}}{{$stuff2}}override two{{/stuff2}}{{/parent}}",
        "new default one, override two"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    PARENT_TEMPLATE(
        null,
        "inheritance",
        "Parent template",
        "Parent templates behave identically to partials when called with no parameters",
        "{}",
        "{{>parent}}|{{<parent}}{{/parent}}",
        "default content|default content"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    RECURSION(
        null,
        "inheritance",
        "Recursion",
        "Recursion in inherited templates",
        "{}",
        "{{<parent}}{{$foo}}override{{/foo}}{{/parent}}",
        "override override override don't recurse"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    MULTI_LEVEL_INHERITANCE(
        null,
        "inheritance",
        "Multi-level inheritance",
        "Top-level substitutions take precedence in multi-level inheritance",
        "{}",
        "{{<parent}}{{$a}}c{{/a}}{{/parent}}",
        "c"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    MULTI_LEVEL_INHERITANCE__NO_SUB_CHILD(
        null,
        "inheritance",
        "Multi-level inheritance, no sub child",
        "Top-level substitutions take precedence in multi-level inheritance",
        "{}",
        "{{<parent}}{{/parent}}",
        "p"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    TEXT_INSIDE_PARENT(
        null,
        "inheritance",
        "Text inside parent",
        "Ignores text inside parent templates, but does parse $ tags",
        "{}",
        "{{<parent}} asdfasd {{$foo}}hmm{{/foo}} asdfasdfasdf {{/parent}}",
        "hmm"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    TEXT_INSIDE_PARENT1(
        null,
        "inheritance",
        "Text inside parent1",
        "Allows text inside a parent tag, but ignores it",
        "{}",
        "{{<parent}} asdfasd asdfasdfasdf {{/parent}}",
        "default content"){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    BLOCK_SCOPE(
        null,
        "inheritance",
        "Block scope",
        "Scope of a substituted block is evaluated in the context of the parent template",
        "{\"fruit\":\"apples\",\"nested\":{\"fruit\":\"bananas\"}}",
        "{{<parent}}{{$block}}I say {{fruit}}.{{/block}}{{/parent}}",
        "I say bananas."){
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

    private InheritanceSpecTemplate(
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

    public static final String DEFAULT_FILE = "inheritance/Default.mustache";
    public static final String VARIABLE_FILE = "inheritance/Variable.mustache";
    public static final String TRIPLE_MUSTACHE_FILE = "inheritance/TripleMustache.mustache";
    public static final String SECTIONS_FILE = "inheritance/Sections.mustache";
    public static final String NEGATIVE_SECTIONS_FILE = "inheritance/NegativeSections.mustache";
    public static final String MUSTACHE_INJECTION_FILE = "inheritance/MustacheInjection.mustache";
    public static final String INHERIT_FILE = "inheritance/Inherit.mustache";
    public static final String OVERRIDDEN_CONTENT_FILE = "inheritance/Overriddencontent.mustache";
    public static final String DATA_DOES_NOT_OVERRIDE_BLOCK_FILE = "inheritance/Datadoesnotoverrideblock.mustache";
    public static final String DATA_DOES_NOT_OVERRIDE_BLOCK_DEFAULT_FILE = "inheritance/Datadoesnotoverrideblockdefault.mustache";
    public static final String OVERRIDDEN_PARENT_FILE = "inheritance/Overriddenparent.mustache";
    public static final String TWO_OVERRIDDEN_PARENTS_FILE = "inheritance/Twooverriddenparents.mustache";
    public static final String OVERRIDE_PARENT_WITH_NEWLINES_FILE = "inheritance/Overrideparentwithnewlines.mustache";
    public static final String INHERIT_INDENTATION_FILE = "inheritance/Inheritindentation.mustache";
    public static final String ONLY_ONE_OVERRIDE_FILE = "inheritance/Onlyoneoverride.mustache";
    public static final String PARENT_TEMPLATE_FILE = "inheritance/Parenttemplate.mustache";
    public static final String RECURSION_FILE = "inheritance/Recursion.mustache";
    public static final String MULTI_LEVEL_INHERITANCE_FILE = "inheritance/Multilevelinheritance.mustache";
    public static final String MULTI_LEVEL_INHERITANCE__NO_SUB_CHILD_FILE = "inheritance/Multilevelinheritancenosubchild.mustache";
    public static final String TEXT_INSIDE_PARENT_FILE = "inheritance/Textinsideparent.mustache";
    public static final String TEXT_INSIDE_PARENT1_FILE = "inheritance/Textinsideparent1.mustache";
    public static final String BLOCK_SCOPE_FILE = "inheritance/Blockscope.mustache";
}
