package com.snaphop.staticmustache.spec;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

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
 * Specification generator
 */
public class SpecGenerator {
    
    PrintStream out = System.out;
    
    public static void main(String[] args) {
        try {
            new SpecGenerator().generateAll();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    interface JavaItem {
        
        String name();
        
        String group();
        
        default String className() {
            return name().replaceAll("[- \\(\\)]", "");
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
            return name().replaceAll("[- \\(\\)]", "_").toUpperCase();
        }
    }
    
    record TemplateList(String group, List<SpecItem> items) implements JavaItem {
        @Override
        public String name() {
            return  StringUtils.capitalize(group()) + "SpecTemplate";
        }
        
    }

    public void generateAll() throws IOException {
        generate("interpolation");
        generate("sections");

    }
    
    public void generate(String group) throws IOException {
        
        String specFile = group + ".yml";
        
        var items = toSpecItems(group, getSpec(specFile));
        
        String javaTemplate = """
                package {{packageName}};
                
                import com.snaphop.staticmustache.spec.SpecModel;
                
                @{{annotation}}(template = "{{templateFileName}}")
                public class {{className}} extends SpecModel {
                }
                """;
        
        Template template = Mustache.compiler()
                .escapeHTML(false)
                .compile(javaTemplate);
        
        int j = 0;
        for (var i : items) {
            out.println(i);
            if (j == 0) {
                File javaDir = Path.of(i.javaFilePath()).toFile().getParentFile();
                File templateDir = Path.of(i.templateFilePath()).toFile().getParentFile();
                cleanDirectory(javaDir);
                cleanDirectory(templateDir);
            }
            String javaCode = template.execute(i);
            Files.writeString(Path.of(i.javaFilePath()), javaCode, StandardOpenOption.CREATE);
            Files.writeString(Path.of(i.templateFilePath()), i.template());
            j++;
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
                        "{{desc}}",
                        "{{json}}",
                        "{{template}}", 
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
                    private final String title;
                    private final String description;
                    private final String json;
                    private final String template;
                    private final String expected;
                    
                    private {{className}}(
                        Class<?> templateClass,
                        String title,
                        String description,
                        String json,
                        String template,
                        String expected) {
                        this.templateClass = templateClass;
                        this.title = title;
                        this.description = description;
                        this.json = json;
                        this.template = template;
                        this.expected = expected;
                    }
                    public Class<?> templateClass() {
                        return templateClass;
                    }
                    public String title() {
                        return this.title;
                    }
                    public String getDescription() {
                        return this.description;
                    }
                    public String getTemplate() {
                        return this.template;
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
    
    void cleanDirectory(File directory) throws IOException {
        if (directory.exists()) {
            try (var s = Files.walk(directory.toPath())) {
                s.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            }
        }
        directory.mkdirs();
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
    



    @SuppressWarnings("unchecked")
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
                 //String s = new ObjectMapper().readValue(json, String.class);
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
