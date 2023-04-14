package io.jstach.apt;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;

import io.jstach.apt.internal.util.ClassRef;
import io.jstach.apt.prism.Prisms;

class CatalogClassWriter {

	private final Set<String> templateClassNames = new TreeSet<>();

	private boolean dirty = true;

	private final ClassRef catalogClass;

	public CatalogClassWriter(String packageName, String catalogName) {
		this(ClassRef.of(packageName, catalogName));
	}

	public CatalogClassWriter(ClassRef catalogClass) {
		this.catalogClass = catalogClass;
	}

	void write(Appendable a) throws IOException {

		String packageName = catalogClass.getPackageName();
		String catalogName = catalogClass.getSimpleName();
		String _TemplateProvider = Prisms.GENERATED_TEMPLATE_PROVIDER_CLASS;
		String _List = List.class.getCanonicalName();
		String _Template = Prisms.TEMPLATE_CLASS;
		String _TemplateConfig = Prisms.TEMPLATE_CONFIG_CLASS;

		a.append("package " + packageName + ";\n");
		a.append("\n");
		a.append("/**\n");
		a.append(" * Generated template catalog.\n");
		a.append(" */\n");
		a.append("public class " + catalogName + " implements " + _TemplateProvider + " {\n");
		a.append("\n");
		a.append("    /**\n");
		a.append("     * Generated template catalog constructor for ServiceLoader.\n");
		a.append("     */\n");
		a.append("    public " + catalogName + "() {\n");
		a.append("    }\n");
		a.append("\n");
		a.append("    @Override\n");
		a.append("    public " + _List + "<" + _Template + "<?>> provideTemplates(" + _TemplateConfig
				+ " templateConfig) {\n");
		a.append("        return " + _List + ".of(//\n");
		a.append(listTemplates("        new ", "(templateConfig)")).append(");\n");
		a.append("    }\n");
		a.append("}\n");
	}

	public void write(Filer filer, Messager messager) {
		try {
			FileObject sourceFile = filer.createSourceFile(catalogClass.requireCanonicalName());
			try (var w = sourceFile.openWriter()) {
				write(w);
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			messager.printMessage(Kind.ERROR, "error writing catalog class: " + ioe.getMessage());
		}
	}

	String listTemplates(String prefix, String suffix) {
		return templateClassNames.stream().map(tn -> prefix + tn + suffix).collect(Collectors.joining(", //\n"));
	}

	void addAll(Collection<? extends ClassRef> templateClasses) {
		for (var tc : templateClasses) {
			this.templateClassNames.add(tc.getCanonicalName());
		}
	}

	boolean addTemplateClasses(Collection<String> templateClassNames) {
		boolean b = this.templateClassNames.addAll(templateClassNames);
		if (b) {
			dirty = b;
		}
		return b;
	}

	public boolean isDirty() {
		return dirty;
	}

}
