package com.snaphop.staticmustache.spec.inheritance;

import com.snaphop.staticmustache.spec.SpecListing;
import java.util.Map;

public enum InheritanceSpecTemplate implements SpecListing {
    DEFAULT(
        Default.class,
        "inheritance",
        "Default",
        "Default content should be rendered if the block isn't overridden",
        "{}",
        "{{$title}}Default title{{/title}}\n",
        "Default title\n",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new Default();
            m.putAll(o);
            var r = DefaultRenderer.of(m);
            return r.renderString();
        }
    },
    VARIABLE(
        Variable.class,
        "inheritance",
        "Variable",
        "Default content renders variables",
        "{\"bar\":\"baz\"}",
        "{{$foo}}default {{bar}} content{{/foo}}\n",
        "default baz content\n",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new Variable();
            m.putAll(o);
            var r = VariableRenderer.of(m);
            return r.renderString();
        }
    },
    TRIPLE_MUSTACHE(
        TripleMustache.class,
        "inheritance",
        "Triple Mustache",
        "Default content renders triple mustache variables",
        "{\"bar\":\"<baz>\"}",
        "{{$foo}}default {{{bar}}} content{{/foo}}\n",
        "default <baz> content\n",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new TripleMustache();
            m.putAll(o);
            var r = TripleMustacheRenderer.of(m);
            return r.renderString();
        }
    },
    SECTIONS(
        Sections.class,
        "inheritance",
        "Sections",
        "Default content renders sections",
        "{\"bar\":{\"baz\":\"qux\"}}",
        "{{$foo}}default {{#bar}}{{baz}}{{/bar}} content{{/foo}}\n",
        "default qux content\n",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new Sections();
            m.putAll(o);
            var r = SectionsRenderer.of(m);
            return r.renderString();
        }
    },
    NEGATIVE_SECTIONS(
        null,
        "inheritance",
        "Negative Sections",
        "Default content renders negative sections",
        "{\"baz\":\"three\"}",
        "{{$foo}}default {{^bar}}{{baz}}{{/bar}} content{{/foo}}\n",
        "default three content\n",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    MUSTACHE_INJECTION(
        MustacheInjection.class,
        "inheritance",
        "Mustache Injection",
        "Mustache injection in default content",
        "{\"bar\":{\"baz\":\"{{qux}}\"}}",
        "{{$foo}}default {{#bar}}{{baz}}{{/bar}} content{{/foo}}\n",
        "default {{qux}} content\n",
        Map.of()
        ){
        public String render(Map<String, Object> o) {
            var m = new MustacheInjection();
            m.putAll(o);
            var r = MustacheInjectionRenderer.of(m);
            return r.renderString();
        }
    },
    INHERIT(
        Inherit.class,
        "inheritance",
        "Inherit",
        "Default content rendered inside inherited templates",
        "{}",
        "{{<include}}{{/include}}\n",
        "default content",
        Map.of(
            
            "include",
            "{{$foo}}default content{{/foo}}"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new Inherit();
            m.putAll(o);
            var r = InheritRenderer.of(m);
            return r.renderString();
        }
    },
    OVERRIDDEN_CONTENT(
        Overriddencontent.class,
        "inheritance",
        "Overridden content",
        "Overridden content",
        "{}",
        "{{<super}}{{$title}}sub template title{{/title}}{{/super}}",
        "...sub template title...",
        Map.of(
            
            "super",
            "...{{$title}}Default title{{/title}}..."
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new Overriddencontent();
            m.putAll(o);
            var r = OverriddencontentRenderer.of(m);
            return r.renderString();
        }
    },
    DATA_DOES_NOT_OVERRIDE_BLOCK(
        Datadoesnotoverrideblock.class,
        "inheritance",
        "Data does not override block",
        "Context does not override argument passed into parent",
        "{\"var\":\"var in data\"}",
        "{{<include}}{{$var}}var in template{{/var}}{{/include}}",
        "var in template",
        Map.of(
            
            "include",
            "{{$var}}var in include{{/var}}"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new Datadoesnotoverrideblock();
            m.putAll(o);
            var r = DatadoesnotoverrideblockRenderer.of(m);
            return r.renderString();
        }
    },
    DATA_DOES_NOT_OVERRIDE_BLOCK_DEFAULT(
        Datadoesnotoverrideblockdefault.class,
        "inheritance",
        "Data does not override block default",
        "Context does not override default content of block",
        "{\"var\":\"var in data\"}",
        "{{<include}}{{/include}}",
        "var in include",
        Map.of(
            
            "include",
            "{{$var}}var in include{{/var}}"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new Datadoesnotoverrideblockdefault();
            m.putAll(o);
            var r = DatadoesnotoverrideblockdefaultRenderer.of(m);
            return r.renderString();
        }
    },
    OVERRIDDEN_PARENT(
        Overriddenparent.class,
        "inheritance",
        "Overridden parent",
        "Overridden parent",
        "{}",
        "test {{<parent}}{{$stuff}}override{{/stuff}}{{/parent}}",
        "test override",
        Map.of(
            
            "parent",
            "{{$stuff}}...{{/stuff}}"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new Overriddenparent();
            m.putAll(o);
            var r = OverriddenparentRenderer.of(m);
            return r.renderString();
        }
    },
    TWO_OVERRIDDEN_PARENTS(
        Twooverriddenparents.class,
        "inheritance",
        "Two overridden parents",
        "Two overridden parents with different content",
        "{}",
        "test {{<parent}}{{$stuff}}override1{{/stuff}}{{/parent}} {{<parent}}{{$stuff}}override2{{/stuff}}{{/parent}}\n",
        "test |override1 default| |override2 default|\n",
        Map.of(
            
            "parent",
            "|{{$stuff}}...{{/stuff}}{{$default}} default{{/default}}|"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new Twooverriddenparents();
            m.putAll(o);
            var r = TwooverriddenparentsRenderer.of(m);
            return r.renderString();
        }
    },
    OVERRIDE_PARENT_WITH_NEWLINES(
        Overrideparentwithnewlines.class,
        "inheritance",
        "Override parent with newlines",
        "Override parent with newlines",
        "{}",
        "{{<parent}}{{$ballmer}}\npeaked\n\n:(\n{{/ballmer}}{{/parent}}",
        "peaked\n\n:(\n",
        Map.of(
            
            "parent",
            "{{$ballmer}}peaking{{/ballmer}}"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new Overrideparentwithnewlines();
            m.putAll(o);
            var r = OverrideparentwithnewlinesRenderer.of(m);
            return r.renderString();
        }
    },
    INHERIT_INDENTATION(
        Inheritindentation.class,
        "inheritance",
        "Inherit indentation",
        "Inherit indentation when overriding a parent",
        "{}",
        "{{<parent}}{{$nineties}}hammer time{{/nineties}}{{/parent}}",
        "stop:\n  hammer time\n",
        Map.of(
            
            "parent",
            "stop:\n  {{$nineties}}collaborate and listen{{/nineties}}\n"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new Inheritindentation();
            m.putAll(o);
            var r = InheritindentationRenderer.of(m);
            return r.renderString();
        }
    },
    ONLY_ONE_OVERRIDE(
        Onlyoneoverride.class,
        "inheritance",
        "Only one override",
        "Override one parameter but not the other",
        "{}",
        "{{<parent}}{{$stuff2}}override two{{/stuff2}}{{/parent}}",
        "new default one, override two",
        Map.of(
            
            "parent",
            "{{$stuff}}new default one{{/stuff}}, {{$stuff2}}new default two{{/stuff2}}"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new Onlyoneoverride();
            m.putAll(o);
            var r = OnlyoneoverrideRenderer.of(m);
            return r.renderString();
        }
    },
    PARENT_TEMPLATE(
        Parenttemplate.class,
        "inheritance",
        "Parent template",
        "Parent templates behave identically to partials when called with no parameters",
        "{}",
        "{{>parent}}|{{<parent}}{{/parent}}",
        "default content|default content",
        Map.of(
            
            "parent",
            "{{$foo}}default content{{/foo}}"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new Parenttemplate();
            m.putAll(o);
            var r = ParenttemplateRenderer.of(m);
            return r.renderString();
        }
    },
    RECURSION(
        null,
        "inheritance",
        "Recursion",
        "Recursion in inherited templates",
        "{}",
        "{{<parent}}{{$foo}}override{{/foo}}{{/parent}}",
        "override override override don't recurse",
        Map.of(
            
            "parent",
            "{{$foo}}default content{{/foo}} {{$bar}}{{<parent2}}{{/parent2}}{{/bar}}"
            ,
            "parent2",
            "{{$foo}}parent2 default content{{/foo}} {{<parent}}{{$bar}}don't recurse{{/bar}}{{/parent}}"
        )
        ){
        public String render(Map<String, Object> o) {
            return "DISABLED TEST";
        }
    },
    MULTI_LEVEL_INHERITANCE(
        Multilevelinheritance.class,
        "inheritance",
        "Multi-level inheritance",
        "Top-level substitutions take precedence in multi-level inheritance",
        "{}",
        "{{<parent}}{{$a}}c{{/a}}{{/parent}}",
        "c",
        Map.of(
            
            "parent",
            "{{<older}}{{$a}}p{{/a}}{{/older}}"
            ,
            "older",
            "{{<grandParent}}{{$a}}o{{/a}}{{/grandParent}}"
            ,
            "grandParent",
            "{{$a}}g{{/a}}"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new Multilevelinheritance();
            m.putAll(o);
            var r = MultilevelinheritanceRenderer.of(m);
            return r.renderString();
        }
    },
    MULTI_LEVEL_INHERITANCE__NO_SUB_CHILD(
        Multilevelinheritancenosubchild.class,
        "inheritance",
        "Multi-level inheritance, no sub child",
        "Top-level substitutions take precedence in multi-level inheritance",
        "{}",
        "{{<parent}}{{/parent}}",
        "p",
        Map.of(
            
            "parent",
            "{{<older}}{{$a}}p{{/a}}{{/older}}"
            ,
            "older",
            "{{<grandParent}}{{$a}}o{{/a}}{{/grandParent}}"
            ,
            "grandParent",
            "{{$a}}g{{/a}}"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new Multilevelinheritancenosubchild();
            m.putAll(o);
            var r = MultilevelinheritancenosubchildRenderer.of(m);
            return r.renderString();
        }
    },
    TEXT_INSIDE_PARENT(
        Textinsideparent.class,
        "inheritance",
        "Text inside parent",
        "Ignores text inside parent templates, but does parse $ tags",
        "{}",
        "{{<parent}} asdfasd {{$foo}}hmm{{/foo}} asdfasdfasdf {{/parent}}",
        "hmm",
        Map.of(
            
            "parent",
            "{{$foo}}default content{{/foo}}"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new Textinsideparent();
            m.putAll(o);
            var r = TextinsideparentRenderer.of(m);
            return r.renderString();
        }
    },
    TEXT_INSIDE_PARENT1(
        Textinsideparent1.class,
        "inheritance",
        "Text inside parent1",
        "Allows text inside a parent tag, but ignores it",
        "{}",
        "{{<parent}} asdfasd asdfasdfasdf {{/parent}}",
        "default content",
        Map.of(
            
            "parent",
            "{{$foo}}default content{{/foo}}"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new Textinsideparent1();
            m.putAll(o);
            var r = Textinsideparent1Renderer.of(m);
            return r.renderString();
        }
    },
    BLOCK_SCOPE(
        Blockscope.class,
        "inheritance",
        "Block scope",
        "Scope of a substituted block is evaluated in the context of the parent template",
        "{\"fruit\":\"apples\",\"nested\":{\"fruit\":\"bananas\"}}",
        "{{<parent}}{{$block}}I say {{fruit}}.{{/block}}{{/parent}}",
        "I say bananas.",
        Map.of(
            
            "parent",
            "{{#nested}}{{$block}}You say {{fruit}}.{{/block}}{{/nested}}"
        )
        ){
        public String render(Map<String, Object> o) {
            var m = new Blockscope();
            m.putAll(o);
            var r = BlockscopeRenderer.of(m);
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

    private InheritanceSpecTemplate(
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
