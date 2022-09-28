package com.snaphop.staticmustache.spec;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.sviperll.staticmustache.GenerateRenderableAdapter;
import com.github.sviperll.staticmustache.TemplateFormatterTypes;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Escaper;
import com.samskivert.mustache.Template;

/**
 * Specification tests
 */
public class SpecGenerator {
    
    PrintStream out = System.out;
    
    interface JavaItem {
        
        String name();
        
        String group();
        
        default String className() {
            return name().replaceAll("-", "").replaceAll(" ", "");
        }
        default String packageName() {
            return SpecModel.class.getPackageName() + "." + group();
        }
        default String fileDir() {
            return "src/main/java/" + packageName().replace('.', '/');
        }
        default String javaFilePath() {
            return fileDir() + "/" + className() + ".java";
        }
        default String packageInfoFilePath() {
            return fileDir() + "/" + "package-info.java";
        }

    }
    public record SpecItem(String name, String group, String desc, String template, 
            String json, Map<String,Object> data, String expected) implements JavaItem {

        String annotation() {
            return GenerateRenderableAdapter.class.getName();
        }
        String templateName() {
            return className();
        }
        String templateFileName() {
            return group() + "/" + templateName() + ".mustache";
        }
        String templateFilePath() {
            return "src/main/resources/" + templateFileName();
        }
        String enumName() {
            return name().replaceAll("-", "_").replaceAll(" ", "_").toUpperCase();
        }
    }
    
    record TemplateList(String group, List<SpecItem> items) implements JavaItem {
        @Override
        public String name() {
            return  StringUtils.capitalize(group()) + "SpecTemplate";
        }
        
    }

    @Test
    public void interpolations() throws IOException {
        
        String group = "interpolation";
        String specFile = group + ".yml";
        
        var items = toSpecItems(group, getSpec(specFile));
        
        String javaTemplate = """
                package {{packageName}};
                
                import com.snaphop.staticmustache.spec.SpecModel;
                
                @{{annotation}}(template = "{{templateFileName}}")
                public class {{className}} extends SpecModel {
                    private static final long serialVersionUID = 1L;
                }
                """;
        
        Template template = Mustache.compiler()
                .escapeHTML(false)
                .compile(javaTemplate);
        
        for (var i : items) {
            out.println(i);
            String javaCode = template.execute(i);
            Path.of(i.javaFilePath()).toFile().getParentFile().mkdir();
            Path.of(i.templateFilePath()).toFile().getParentFile().mkdir();
            Files.writeString(Path.of(i.javaFilePath()), javaCode, StandardOpenOption.CREATE);
            Files.writeString(Path.of(i.templateFilePath()), i.template());
        }
        
        String enumTemplate = """
                package {{packageName}};
                
                import com.snaphop.staticmustache.spec.SpecListing;
                import java.util.Map;
                
                public enum {{className}} implements SpecListing {
                    {{#items}}
                    {{enumName}}(
                        {{className}}.class, 
                        "{{name}}", 
                        "{{json}}",
                        "{{expected}}"){
                        public String render(Map<String, Object> o) {
                            var m = new {{className}}();
                            m.putAll(o);
                            var r = {{className}}Renderer.of(m);
                            return r.renderString();
                        }
                    },
                    {{/items}}
                    ;
                    private final Class<?> templateClass;
                    private final String json;
                    private final String title;
                    private final String expected;
                    
                    private {{className}}(
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
                    
                    {{#items}}
                    public static final String {{enumName}}_FILE = "{{templateFileName}}";
                    {{/items}}
                }
                """;
        
        template = Mustache.compiler()
                .escapeHTML(false)
                .withEscaper(new Escaper() {
                    @Override
                    public String escape(String raw) {
                        return StringEscapeUtils.ESCAPE_JAVA.translate(raw);
                    }
                })
                .compile(enumTemplate);
        var templateList = new TemplateList(group, items);
        String enumCode = template.execute(templateList);
        Files.writeString(Path.of(templateList.javaFilePath()), enumCode);

        
        Map<String, String> model = Map.of("formatterAnnotation", TemplateFormatterTypes.class.getName(),
                "packageName", templateList.packageName());
        
        String packageInfoTemplate = """
                @{{formatterAnnotation}}(patterns = ".*")
                package {{packageName}};
                """;

        String packageInfoCode = Mustache.compiler().escapeHTML(false).compile(packageInfoTemplate).execute(model);
        Files.writeString(Path.of(templateList.packageInfoFilePath()), packageInfoCode);
        
    }

//    @Ignore
//    @Test
//    public void sections() throws IOException {
//        run(getSpec("sections.yml"));
//    }
//
//    @Ignore
//    @Test
//    public void delimiters() throws IOException {
//        run(getSpec("delimiters.yml"));
//    }

    // @Test
    // public void inverted() throws IOException {
    // run(getSpec("inverted.yml"));
    // }
    //
    // @Test
    // public void partials() throws IOException {
    // run(getSpec("partials.yml"));
    // }
    //
    // @Test
    // public void lambdas() throws IOException {
    // run(getSpec("~lambdas.yml"));
    // }
    //
    // @Test
    // public void inheritance() throws IOException {
    // run(getSpec("~inheritance.yml"));
    // }
    



    private List<SpecItem> toSpecItems(String group, JsonNode spec) throws IOException {

        List<SpecItem> list = new ArrayList<>();
        for (final JsonNode test : spec.get("tests")) {
            String name = test.get("name").asText();
            String desc = test.get("desc").asText();
            String template = test.get("template").asText();
            String expected = test.get("expected").asText();
            JsonNode data = test.get("data");
            String json = data.toString();
            Map<String, Object> _data;
             if (json.startsWith("{")) {
                 _data = (Map<String,Object>) new ObjectMapper().readValue(json, Map.class);
                 list.add(new SpecItem(name, group, desc, template, json, _data, expected));
             } 
             else {
                 String s = new ObjectMapper().readValue(json, String.class);
             }
        }
        return list;
        // assertFalse(fail > 0);
        
    }


    protected String transformOutput(String output) {
        return output.replaceAll("\\s+", "");
    }

    // protected DefaultMustacheFactory createMustacheFactory(final JsonNode
    // test) {
    // return new DefaultMustacheFactory("/spec/specs") {
    // @Override
    // public Reader getReader(String resourceName) {
    // JsonNode partial = test.get("partials").get(resourceName);
    // return new StringReader(partial == null ? "" : partial.asText());
    // }
    // };
    // }

    private JsonNode getSpec(String spec) throws IOException {
        return new YAMLFactory(new YAMLMapper())
                .createParser(new InputStreamReader(SpecGenerator.class.getResourceAsStream("/spec-1.3.0/specs/" + spec)))
                .readValueAsTree();
    }

}
