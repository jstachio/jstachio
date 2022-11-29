package io.jstach.apt;

import static io.jstach.apt.prism.Prisms.APPENDER_CLASS;
import static io.jstach.apt.prism.Prisms.ESCAPER_CLASS;
import static io.jstach.apt.prism.Prisms.FILTER_CHAIN_CLASS;
import static io.jstach.apt.prism.Prisms.FORMATTER_CLASS;
import static io.jstach.apt.prism.Prisms.TEMPLATE_CLASS;
import static io.jstach.apt.prism.Prisms.TEMPLATE_CONFIG_CLASS;
import static io.jstach.apt.prism.Prisms.TEMPLATE_INFO_CLASS;
import static io.jstach.apt.prism.Prisms.TEMPLATE_PROVIDER_CLASS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import io.jstach.apt.GenerateRendererProcessor.RendererModel;
import io.jstach.apt.TemplateCompilerLike.TemplateCompilerType;
import io.jstach.apt.internal.AnnotatedException;
import io.jstach.apt.internal.CodeAppendable;
import io.jstach.apt.internal.FormatterTypes.FormatCallType;
import io.jstach.apt.internal.NamedTemplate;
import io.jstach.apt.internal.ProcessingException;
import io.jstach.apt.internal.context.TemplateCompilerContext;
import io.jstach.apt.internal.context.VariableContext;
import io.jstach.apt.internal.util.ClassRef;
import io.jstach.apt.prism.JStacheContentTypePrism;
import io.jstach.apt.prism.JStacheFormatterPrism;

class TemplateClassWriter {

	private final CodeWriter codeWriter;

	private final TextFileObject templateLoader;

	private final FormatCallType formatCallType;

	final String idt = "\n        ";

	final String _F_Formatter = Function.class.getName() + "< /* @Nullable */ Object, String>";

	final String _F_Escaper = Function.class.getName() + "<String, String>";

	final String _Appendable = Appendable.class.getName();

	TemplateClassWriter(CodeWriter compilerManager, TextFileObject templateLoader, FormatCallType formatCallType) {
		this.codeWriter = compilerManager;
		this.templateLoader = templateLoader;
		this.formatCallType = formatCallType;
	}

	void println(String s) {
		codeWriter.println(s);
	}

	void writeRenderableAdapterClass(RendererModel model) throws IOException, ProcessingException, AnnotatedException {

		boolean jstachio = formatCallType == FormatCallType.JSTACHIO;

		var element = model.element();
		var contentTypeElement = model.contentTypeElement();
		var formatterTypeElement = model.formatterTypeElement();
		var ifaces = model.ifaces();
		var renderClassRef = model.rendererClassRef();
		ClassRef modelClassRef = ClassRef.of(element);
		String className = modelClassRef.getCanonicalName();
		if (className == null) {
			throw new AnnotatedException(element, "Anonymous classes can not be used as models");
		}
		String packageName = modelClassRef.getPackageName();
		/*
		 * TODO we should make this whole "provides" pattern DRY
		 */
		JStacheContentTypePrism contentTypePrism = JStacheContentTypePrism.getInstanceOn(contentTypeElement);
		assert contentTypePrism != null;
		JStacheFormatterPrism formatterPrism = JStacheFormatterPrism.getInstanceOn(formatterTypeElement);
		assert formatterPrism != null;

		List<String> interfaces = new ArrayList<>();
		if (jstachio) {
			interfaces.add(TEMPLATE_CLASS + "<" + className + ">");
			interfaces.add(TEMPLATE_INFO_CLASS);
			interfaces.add(TEMPLATE_PROVIDER_CLASS);
			interfaces.add(FILTER_CHAIN_CLASS);
		}
		interfaces.addAll(ifaces.templateInterfaces());
		String implementsString = interfaces.stream().collect(Collectors.joining(",\n    "));

		String rendererAnnotated = ifaces.templateAnnotations().stream().map(ta -> "@" + ta + "\n")
				.collect(Collectors.joining());

		String rendererImplements = implementsString.isBlank() ? "" : " implements " + implementsString;

		String modifier = element.getModifiers().contains(Modifier.PUBLIC) ? "public " : "";

		String rendererClassSimpleName = renderClassRef.getSimpleName();

		NamedTemplate namedTemplate = model.namedTemplate();

		String templateName = namedTemplate.name();
		String templatePath = model.pathConfig().resolveTemplatePath(model.namedTemplate().path());
		String templateString = namedTemplate.template();

		String templateStringJava = CodeAppendable.stringConcat(templateString);

		String _Appender = APPENDER_CLASS + "<" + _Appendable + ">";

		String _Formatter = jstachio ? FORMATTER_CLASS : _F_Formatter;
		String _Escaper = jstachio ? ESCAPER_CLASS : _F_Escaper;

		String contentTypeProvideCall = contentTypeElement.getQualifiedName() + "." + contentTypePrism.providesMethod()
				+ "()";
		String formatterProvideCall = formatterTypeElement.getQualifiedName() + "." + formatterPrism.providesMethod()
				+ "()";

		println("package " + packageName + ";");
		println("");
		println("/**");
		println(" * Generated Renderer.");
		println(" */");
		println("// @javax.annotation.Generated(\"" + GenerateRendererProcessor.class.getName() + "\")");
		if (!rendererAnnotated.isBlank()) {
			println(rendererAnnotated);
		}
		println(modifier + "class " + rendererClassSimpleName + rendererImplements + " {");

		println("    /**");
		println("     * @hidden");
		println("     */");
		println("    public static final String TEMPLATE_PATH = \"" + templatePath + "\";");
		println("");
		println("    /**");
		println("     * @hidden");
		println("     */");
		println("");
		println("    public static final String TEMPLATE_STRING = " + templateStringJava + ";");
		println("");
		println("    /**");
		println("     * @hidden");
		println("     */");
		println("    public static final String TEMPLATE_NAME = \"" + templateName + "\";");
		println("");
		println("    /**");
		println("     * @hidden");
		println("     */");
		println("    public static final Class<?> MODEL_CLASS = " + className + ".class;");
		println("");
		println("    /**");
		println("     * @hidden");
		println("     */");
		println("    private static final " + rendererClassSimpleName + " INSTANCE = new " + rendererClassSimpleName
				+ "();");
		println("");
		println("    /**");
		println("     * @hidden");
		println("     */");
		println("    private final " + _Formatter + " formatter;");
		println("");
		println("    /**");
		println("     * @hidden");
		println("     */");
		println("    private final " + _Escaper + " escaper;");
		println("");
		println("    /**");
		println("     * Renderer constructor for manual wiring.");
		println("     * @param formatter formatter if null the static formatter will be used.");
		println("     * @param escaper escaper if null the static escaper will be used");
		println("     */");
		println("    public " + rendererClassSimpleName + "(");
		println("        /* @Nullable */ " + _F_Formatter + " formatter,");
		println("        /* @Nullable */ " + _F_Escaper + " escaper) {");

		println("        this.formatter = " + (!jstachio ? "formatter != null ? formatter : (i -> \"\" + i);"
				: _Formatter + ".of(formatter != null ? formatter : " + formatterProvideCall + ");"));
		println("        this.escaper = " + (!jstachio ? "escaper != null ? escaper : (i -> i);"
				: _Escaper + ".of(escaper != null ? escaper : " + contentTypeProvideCall + ");"));

		println("    }");
		println("");
		println("    /**");
		println("     * Renderer constructor for reflection (use of() instead).");
		println("     * For programmatic consider using {@link #of()} for a shared singleton.");
		println("     */");
		println("    public " + rendererClassSimpleName + "() {");
		println("        this(null, null);");
		println("    }");
		println("");
		if (jstachio)
			println("    @Override");
		println("    public void execute(" + className + " model, Appendable a) throws java.io.IOException {");
		println("        execute(model, a, templateFormatter(), templateEscaper());");
		println("    }");
		println("");
		if (jstachio)
			println("    @Override");
		println("    public void execute(" //
				+ idt + className + " model, " //
				+ idt + _Appendable + " a, " //
				+ idt + _Formatter + " formatter" + "," //
				+ idt + _Escaper + " escaper" + ") throws java.io.IOException {");
		if (jstachio) {
			println("        render(model, a, formatter, escaper, templateAppender());");
		}
		else {
			println("        render(model, a, formatter, escaper);");
		}
		println("    }");

		println("");
		if (jstachio)
			println("    @Override");
		println("    public boolean supportsType(Class<?> type) {");
		println("        return MODEL_CLASS.isAssignableFrom(type);");
		println("    }");
		println("");
		if (jstachio) {
			println("    @Override");
			println("    public java.util.List<" + TEMPLATE_CLASS + "<?>> " + "provideTemplates("
					+ TEMPLATE_CONFIG_CLASS + " templateConfig ) {");
			println("        return java.util.List.of(" + TEMPLATE_CONFIG_CLASS + ".empty() == templateConfig ? "
					+ "INSTANCE :  new " + rendererClassSimpleName + "(templateConfig));");
			println("    }");
			println("");
		}
		if (jstachio)
			println("    @Override");
		println("    public String " + "templatePath() {");
		println("        return TEMPLATE_PATH;");
		println("    }");

		if (jstachio)
			println("    @Override");
		println("    public String " + "templateName() {");
		println("        return TEMPLATE_NAME;");
		println("    }");

		if (jstachio)
			println("    @Override");
		println("    public String " + "templateString() {");
		println("        return TEMPLATE_STRING;");
		println("    }");

		if (jstachio)
			println("    @Override");
		println("    public Class<?> " + "templateContentType() {");
		println("        return " + contentTypeElement.getQualifiedName() + ".class;");
		println("    }");

		if (jstachio)
			println("    @Override");
		println("    public  " + _Escaper + " templateEscaper() {");
		println("        return this.escaper;");
		println("    }");

		if (jstachio)
			println("    @Override");
		println("    public " + _Formatter + " templateFormatter() {");
		println("        return this.formatter;");
		println("    }");
		println("");
		if (jstachio) {
			println("    /**");
			println("     * Appender.");
			println("     * @return appender for writing unescaped variables.");
			println("     */");
			println("    public " + _Appender + " templateAppender() {");
			println("        return " + APPENDER_CLASS + ".defaultAppender();");
			println("    }");
			println("");
		}
		println("    /**");
		println("     * Model class.");
		println("     * @return class used as model (annotated with JStache).");
		println("     */");
		if (jstachio)
			println("    @Override");
		println("    public Class<?> modelClass() {");
		println("        return MODEL_CLASS;");
		println("    }");
		println("");
		if (jstachio) {
			println("    @SuppressWarnings(\"unchecked\")");
			println("    @Override");
			println("    public void process(Object model, Appendable appendable) throws java.io.IOException {");
			println("        execute( (" + className + ") model, appendable);");
			println("    }");
			println("");
			println("    @Override");
			println("    public boolean isBroken(Object model) {");
			println("        return !supportsType(model.getClass());");
			println("    }");
			println("");
			println("    /**");
			println("     * Renderer constructor using config.");
			println("     * @param templateConfig config that has collaborators");
			println("     */");
			println("    public " + rendererClassSimpleName + "(" + TEMPLATE_CONFIG_CLASS + " templateConfig) {");
			println("        this(templateConfig.formatter(), templateConfig.escaper());");
			println("    }");
			println("");
		}
		println("    /**");
		println("     * Convience static factory that will reuse the same singleton instance.");
		println("     * @return renderer same as calling no-arg constructor but is cached with singleton instance");
		println("     */");
		println("    public static " + rendererClassSimpleName + " of() {");
		println("        return INSTANCE;");
		println("    }");
		println("");
		writeRendererDefinitionMethod(TemplateCompilerType.SIMPLE, model);
		println("}");
	}

	private void writeRendererDefinitionMethod(TemplateCompilerType templateCompilerType, RendererModel model)
			throws IOException, ProcessingException, AnnotatedException {

		boolean jstachio = formatCallType == FormatCallType.JSTACHIO;

		var element = model.element();

		VariableContext variables = VariableContext.createDefaultContext();
		String dataName = variables.introduceNewNameLike("data");
		String className = element.getQualifiedName().toString();
		String _Appender = APPENDER_CLASS;

		String _Escaper = jstachio ? _Appender + "<? super A>" : _F_Escaper;
		String _Formatter = jstachio ? FORMATTER_CLASS : _F_Formatter;

		String _A = "<A extends " + _Appendable + ">";

		println("    /**");
		println("     * Renders the passed in model.");
		if (jstachio)
			println("     * @param <A> appendable type.");
		println("     * @param " + dataName + " model");
		println("     * @param " + variables.unescapedWriter() + " appendable to write to.");
		println("     * @param " + variables.formatter() + " formats variables before they are passed to the escaper.");
		println("     * @param " + variables.escaper() + " used to write escaped variables.");
		if (jstachio)
			println("     * @param " + variables.appender() + " used to write unescaped variables.");
		println("     * @throws java.io.IOException if an error occurs while writing to the appendable");
		println("     */");
		if (jstachio) {
			println("    public static " + _A + " void render(" //
					+ idt + className + " " + dataName + ", " //
					+ idt + "A" + " " + variables.unescapedWriter() + "," //
					+ idt + _Formatter + " " + variables.formatter() + "," //
					+ idt + _Escaper + " " + variables.escaper() + "," //
					+ idt + _Appender + "<A> " + variables.appender() + ") throws java.io.IOException {");
		}
		else {
			println("    public static  void render(" //
					+ idt + className + " " + dataName + ", " //
					+ idt + _Appendable + " " + variables.unescapedWriter() + "," //
					+ idt + _Formatter + " " + variables.formatter() + "," //
					+ idt + _Escaper + " " + variables.escaper() + ") throws java.io.IOException {");
		}
		TemplateCompilerContext context = codeWriter.createTemplateContext(model.namedTemplate(), element, dataName,
				variables, model.flags());
		codeWriter.compileTemplate(templateLoader, context, templateCompilerType);
		println("");
		println("    }");

	}

}