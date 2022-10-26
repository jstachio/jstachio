package io.jstach.spec.generator;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Escaper;

import io.jstach.annotation.JStacheFormatterTypes;

import com.samskivert.mustache.Template;

/**
 * Specification generator
 */
public class SpecGenerator {

	static PrintStream out = System.out;

	public static void main(String[] args) {
		try {
			out.println("Start");
			new SpecGenerator().generateAll();
		}
		catch (Throwable e) {
			e.printStackTrace();
			// throw new UncheckedIOException(e);
		}
	}

	interface JavaItem {

		String name();

		SpecGroup group();

		default String className() {
			return name().replaceAll("[- \\(\\),]", "");
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

		default boolean enabled() {
			return group().enabled() && !group().ignores().contains(className());
		}

	}

	record ClassRef(Class<?> klass) {
		public String namespace() {
			return klass.getCanonicalName();
		}

		public String name() {
			return klass.getSimpleName();
		}
	}

	record SpecItem(SpecGroup group, String name, String desc, String template, String json, Map<String, Object> data,
			String expected, Map<String, SpecPartial> partialMap) implements JavaItem {

		ClassRef specModelClass() {
			return new ClassRef(SpecModel.class);
		}

		ClassRef annotation() {
			return new ClassRef(io.jstach.annotation.JStache.class);
		}

		ClassRef templatePathsAnnotation() {
			return new ClassRef(io.jstach.annotation.JStachePartials.class);
		}

		ClassRef templatePathAnnotation() {
			return new ClassRef(io.jstach.annotation.JStachePartial.class);
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
			return name().replaceAll("[- \\(\\),]", "_").toUpperCase();
		}

		List<SpecPartial> partials() {
			return List.copyOf(partialMap.values());
		}

		boolean hasPartials() {
			return !partials().isEmpty();
		}
	}

	record TemplateList(SpecGroup group, List<SpecItem> items) implements JavaItem {
		@Override
		public String name() {
			return StringUtils.capitalize(group().name()) + "SpecTemplate";
		}

		public ClassRef specListingClass() {
			return new ClassRef(SpecListing.class);
		}
	}

	enum SpecGroup {

		interpolation, sections, inheritance() {
			@Override
			String fileName() {
				return "~inheritance.yml";
			}

			@Override
			Set<String> ignores() {
				return Set.of("Recursion", "NegativeSections");
			}

			@Override
			boolean enabled() {
				return true;
			}
		},
		inverted, partials() {
			@Override
			boolean enabled() {
				return true;
			}

			@Override
			Set<String> ignores() {
				return Set.of("FailedLookup", "Recursion");
			}
		};

		boolean enabled() {
			return true;
		}

		String fileName() {
			return name() + ".yml";
		}

		Set<String> ignores() {
			return Set.of();
		}

		Set<String> includes() {
			return Set.of();
		}

	}

	public void generateAll() throws IOException {
		generate(SpecGroup.interpolation);
		generate(SpecGroup.sections);
		generate(SpecGroup.inheritance);
		generate(SpecGroup.inverted);
		generate(SpecGroup.partials);

	}

	protected void generate(SpecGroup group) throws IOException {

		String specFile = group.fileName();

		var items = toSpecItems(group, getSpec(specFile));

		String javaTemplate = """
				package {{packageName}};

				import {{specModelClass.namespace}};
				import {{annotation.namespace}};
				{{#hasPartials}}
				import {{templatePathsAnnotation.namespace}};
				import {{templatePathAnnotation.namespace}};
				{{/hasPartials}}

				@{{annotation.name}}(template = "{{templateFileName}}")
				{{#hasPartials}}
				@{{templatePathsAnnotation.name}}({
				{{#partials}}
				@{{templatePathAnnotation.name}}(name="{{name}}", template="{{path}}"),
				{{/partials}}
				})
				{{/hasPartials}}
				public class {{className}} extends SpecModel {
				}
				""";

		Template template = Mustache.compiler().escapeHTML(false).withEscaper(new Escaper() {
			@Override
			public String escape(String raw) {
				return StringEscapeUtils.ESCAPE_JAVA.translate(raw);
			}
		}).compile(javaTemplate);

		int j = 0;
		for (var i : items) {
			if (j == 0) {
				File javaDir = Path.of(i.javaFilePath()).toFile().getParentFile();
				File templateDir = Path.of(i.templateFilePath()).toFile().getParentFile();
				cleanDirectory(javaDir);
				cleanDirectory(templateDir);
			}
			if (i.enabled()) {
				String javaCode = template.execute(i);
				Files.writeString(Path.of(i.javaFilePath()), javaCode, StandardOpenOption.CREATE);
				Files.writeString(Path.of(i.templateFilePath()), i.template());
				j++;
			}
			else {
				out.println("Skipping: " + i);
			}
		}

		String enumTemplate = """
				package {{packageName}};

				import {{specListingClass.namespace}};
				import java.util.Map;

				public enum {{className}} implements {{specListingClass.name}} {
				    {{#items}}
				    {{enumName}}(
				        {{#enabled}}{{className}}.class{{/enabled}}{{^enabled}}null{{/enabled}},
				        "{{group}}",
				        "{{name}}",
				        "{{desc}}",
				        "{{json}}",
				        "{{template}}",
				        "{{expected}}",
				        {{#hasPartials}}
				        Map.of(
				            {{#partials}}
				            {{^-first}},{{/-first}}
				            "{{name}}",
				            "{{path}}"
				            {{/partials}}
				        )
				        {{/hasPartials}}
				        {{^hasPartials}}
				        Map.of()
				        {{/hasPartials}}
				        ){
				        public String render(Map<String, Object> o) {
				            {{#enabled}}
				            var m = new {{className}}();
				            m.putAll(o);
				            var r = {{className}}Renderer.of(m);
				            return r.renderString();
				            {{/enabled}}
				            {{^enabled}}
				            return "DISABLED TEST";
				            {{/enabled}}
				        }
				    },
				    {{/items}}
				    ;
				    private final Class<?> modelClass;
				    private final String group;
				    private final String title;
				    private final String description;
				    private final String json;
				    private final String template;
				    private final String expected;
				    private final Map<String,String> partials;

				    private {{className}}(
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

				    {{#items}}
				    public static final String {{enumName}}_FILE = "{{templateFileName}}";
				    {{/items}}
				}
				""";

		template = Mustache.compiler().escapeHTML(false).withEscaper(new Escaper() {
			@Override
			public String escape(String raw) {
				return StringEscapeUtils.ESCAPE_JAVA.translate(raw);
			}
		}).compile(enumTemplate);
		var templateList = new TemplateList(group, items);
		String enumCode = template.execute(templateList);
		Files.writeString(Path.of(templateList.javaFilePath()), enumCode);

		Map<String, String> model = Map.of("formatterAnnotation", JStacheFormatterTypes.class.getName(), "packageName",
				templateList.packageName());

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

	@SuppressWarnings("unchecked")
	private List<SpecItem> toSpecItems(SpecGroup group, JsonNode spec) throws IOException {

		Set<String> names = new LinkedHashSet<>();
		int nameIncrement = 1;

		List<SpecItem> list = new ArrayList<>();
		for (final JsonNode test : spec.get("tests")) {
			String name = test.get("name").asText();
			String desc = test.get("desc").asText();
			String template = test.get("template").asText();
			String expected = test.get("expected").asText();
			JsonNode data = test.get("data");
			String json = data.toString();

			Map<String, SpecPartial> partials = new LinkedHashMap<>();
			JsonNode pn = test.get("partials");

			if (pn != null) {
				var fields = pn.fields();
				while (fields.hasNext()) {
					var e = fields.next();
					String n = e.getKey();
					var p = e.getValue();
					String pt = p.asText();
					SpecPartial partial = new SpecPartial(n, pt);
					partials.put(n, partial);
				}
			}
			Map<String, Object> _data;

			if (names.contains(name)) {
				name = name + nameIncrement++;
			}
			else {
				names.add(name);
			}
			if (json.startsWith("{")) {
				_data = (Map<String, Object>) new ObjectMapper().readValue(json, Map.class);
				list.add(new SpecItem(group, name, desc, template, json, _data, expected, partials));
			}
			else {
				out.println(String.format("Skipping because data is not valid json.  group: %s, name: %s, json: %s",
						group, name, json));
				// String s = new ObjectMapper().readValue(json, String.class);
			}
		}
		return list;

	}

	protected String transformOutput(String output) {
		return output.replaceAll("\\s+", "");
	}

	private JsonNode getSpec(String spec) throws IOException {
		var is = SpecGenerator.class.getResourceAsStream("/spec-1.3.0/specs/" + spec);
		if (is == null) {
			throw new IOException("Spec is missing. spec: " + spec);
		}
		return new YAMLFactory(new YAMLMapper()).createParser(new InputStreamReader(is)).readValueAsTree();
	}

}
