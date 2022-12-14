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
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.GenerateRendererProcessor.RendererModel;
import io.jstach.apt.TemplateCompilerLike.TemplateCompilerType;
import io.jstach.apt.internal.AnnotatedException;
import io.jstach.apt.internal.CodeAppendable;
import io.jstach.apt.internal.FormatterTypes.FormatCallType;
import io.jstach.apt.internal.NamedTemplate;
import io.jstach.apt.internal.ProcessingException;
import io.jstach.apt.internal.context.JavaLanguageModel;
import io.jstach.apt.internal.context.TemplateCompilerContext;
import io.jstach.apt.internal.context.VariableContext;
import io.jstach.apt.internal.util.ClassRef;
import io.jstach.apt.internal.util.ToStringTypeVisitor;
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

	enum GeneratedMethod {

		execute, templateFormatter, templateEscaper;

		static EnumMap<GeneratedMethod, ExecutableElement> find(TypeElement parentClass) {
			Elements es = JavaLanguageModel.getInstance().getElements();
			EnumMap<GeneratedMethod, ExecutableElement> em = new EnumMap<>(GeneratedMethod.class);
			for (var ee : ElementFilter.methodsIn(es.getAllMembers(parentClass))) {
				for (var m : values()) {
					if (m.isMatch(ee)) {
						em.put(m, ee);
					}
				}
			}
			return em;

		}

		public boolean gen(Set<GeneratedMethod> gm) {
			return !gm.contains(this);
		}

		public boolean isMatch(ExecutableElement e) {
			var jlm = JavaLanguageModel.getInstance();

			if (!this.name().equals(e.getSimpleName().toString())) {
				return false;
			}
			if (!e.getModifiers().contains(Modifier.PUBLIC) || e.getModifiers().contains(Modifier.ABSTRACT)) {
				return false;
			}
			var appendable = jlm.getElements().getTypeElement(Appendable.class.getName()).asType();

			int parameters = switch (this) {
				case execute -> 2;
				case templateFormatter, templateEscaper -> 0;
			};

			if (e.getParameters().size() != parameters) {
				return false;
			}
			return switch (this) {
				case execute -> e.getReturnType().getKind() == TypeKind.VOID
						&& jlm.isSameType(e.getParameters().get(1).asType(), appendable);
				case templateFormatter, templateEscaper -> e.getParameters().isEmpty();
			};
		}

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

		String rendererAnnotated = ifaces.templateAnnotations().stream().map(ta -> "@" + ta)
				.collect(Collectors.joining("\n"));

		String rendererImplements = implementsString.isBlank() ? "" : " implements " + implementsString;

		String rendererExtends = "";

		TypeElement extendsElement = ifaces.extendsElement();
		Set<GeneratedMethod> generatedMethods = Set.of();
		if (extendsElement != null && !Object.class.getName().equals(extendsElement.getQualifiedName().toString())) {
			String name = extendsElement.getQualifiedName().toString();
			String extendsDeclare = name;
			if (extendsElement.getTypeParameters().size() == 1) {
				extendsDeclare = name + "<" + className + ">";
			}
			rendererExtends = " extends " + extendsDeclare;
			generatedMethods = GeneratedMethod.find(extendsElement).keySet();
		}

		String modifier = element.getModifiers().contains(Modifier.PUBLIC) ? "public " : "";

		String rendererClassSimpleName = renderClassRef.getSimpleName();

		NamedTemplate namedTemplate = model.namedTemplate();

		String templateName = (packageName.isEmpty() ? "" : packageName + ".") + rendererClassSimpleName;
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
		println(modifier + "class " + rendererClassSimpleName + rendererExtends + rendererImplements + " {");

		println("    /**");
		println("     * Template path.");
		println("     * @hidden");
		println("     */");
		println("    public static final String TEMPLATE_PATH = \"" + templatePath + "\";");
		println("");
		println("    /**");
		println("     * Inline template string copied.");
		println("     * @hidden");
		println("     */");
		println("");
		println("    public static final String TEMPLATE_STRING = " + templateStringJava + ";");
		println("");
		println("    /**");
		println("     * Template name. Do not rely on this.");
		println("     * @hidden");
		println("     */");
		println("    public static final String TEMPLATE_NAME = \"" + templateName + "\";");
		println("");
		println("    /**");
		println("     * The models class. Use {@link #modelClass()} instead.");
		println("     * @hidden");
		println("     */");
		println("    public static final Class<?> MODEL_CLASS = " + className + ".class;");
		println("");
		println("    /**");
		println("     * The instance. Use {@link {@link #of()} instead.");
		println("     * @hidden");
		println("     */");
		println("    private static final " + rendererClassSimpleName + " INSTANCE = new " + rendererClassSimpleName
				+ "();");
		println("");
		println("    /**");
		println("     * Formatter. ");
		println("     * @hidden");
		println("     */");
		println("    private final " + _Formatter + " formatter;");
		println("");
		println("    /**");
		println("     * Escaper. ");
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
		println("        super();");
		println("        this.formatter = __formatter(formatter);");
		println("        this.escaper = __escaper(escaper);");
		println("    }");
		println("");
		if (jstachio) {
			println("    private static " + _Formatter + " __formatter(" + "/* @Nullable */ " + _F_Formatter
					+ " formatter) {");
			println("        return " + _Formatter + ".of(formatter != null ? formatter : " + formatterProvideCall
					+ ");");
			println("    }");
		}
		else {
			println("    private static " + _F_Formatter + " __formatter(" + "/* @Nullable */ " + _F_Formatter
					+ " formatter) {");
			println("        return formatter != null ? formatter : (i -> \"\" + i);");
			println("    }");
		}
		println("");
		if (jstachio) {
			println("    private static " + _Escaper + " __escaper(" + "/* @Nullable */ " + _F_Escaper + " escaper) {");
			println("        return " + _Escaper + ".of(escaper != null ? escaper : " + contentTypeProvideCall + ");");
			println("    }");
		}
		else {
			println("    private static " + _F_Escaper + " __escaper(" + "/* @Nullable */ " + _F_Escaper
					+ " escaper) {");
			println("        return escaper != null ? escaper : (i -> i);");
			println("    }");
		}
		println("");
		println("    /**");
		println("     * Renderer constructor for reflection (use of() instead).");
		println("     * For programmatic consider using {@link #of()} for a shared singleton.");
		println("     */");
		println("    public " + rendererClassSimpleName + "() {");
		println("        this(null, null);");
		println("    }");
		println("");
		if (jstachio && GeneratedMethod.execute.gen(generatedMethods)) {
			println("    @Override");
		}
		else if (GeneratedMethod.execute.gen(generatedMethods)) {
			println("    /**");
			println("     * Renders the passed in model.");
			println("     * @param model a model assumed never to be <code>null</code>.");
			println("     * @param appendable the appendable to write to.");
			println("     * @throws IOException if there is an error writing to the appendable");
			println("     */");
		}
		if (GeneratedMethod.execute.gen(generatedMethods)) {
			println("    public void execute(" + className + " model, Appendable a) throws java.io.IOException {");
			println("        execute(model, a, templateFormatter(), templateEscaper());");
			println("    }");
			println("");
		}
		if (jstachio)
			println("    @Override");
		else {
			println("    /**");
			println("     * Renders the passed in model.");
			println("     * @param model a model assumed never to be <code>null</code>.");
			println("     * @param a appendable to write to.");
			println("     * @param formatter formats variables before they are passed to the escaper");
			println("     * @param escaper used to write escaped variables");
			println("     * @throws IOException if an error occurs while writing to the appendable");
			println("     */");
		}
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
		else {
			println("    /**");
			println("     * If this template support the model class");
			println("     * @param type model class.");
			println("     * @return true if the renderer supports the class");
			println("     */");
		}
		println("    public boolean supportsType(Class<?> type) {");
		println("        return MODEL_CLASS.isAssignableFrom(type);");
		println("    }");
		println("");
		if (jstachio) {
			println("    /**");
			println("     * Needed for jstachio runtime.");
			println("     * @hidden");
			println("     */");
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
		else {
			println("    /**");
			println("     * Template path.");
			println("     * @return template path of resource or pseudo inline path");
			println("     */");
		}
		println("    public String " + "templatePath() {");
		println("        return TEMPLATE_PATH;");
		println("    }");

		if (jstachio)
			println("    @Override");
		else {
			println("    /**");
			println("     * Logical template name.");
			println("     * @return template name");
			println("     */");
		}
		println("    public String " + "templateName() {");
		println("        return TEMPLATE_NAME;");
		println("    }");

		if (jstachio)
			println("    @Override");
		else {
			println("    /**");
			println("     * Template contents or blank if path.");
			println("     * @return inline template");
			println("     */");
		}
		println("    public String " + "templateString() {");
		println("        return TEMPLATE_STRING;");
		println("    }");

		if (jstachio) {
			println("    @Override");
			println("    public Class<?> " + "templateContentType() {");
			println("        return " + contentTypeElement.getQualifiedName() + ".class;");
			println("    }");
		}
		if (GeneratedMethod.templateEscaper.gen(generatedMethods)) {
			if (jstachio)
				println("    @Override");
			else {
				println("    /**");
				println("     * Current escaper.");
				println("     * @return escaper");
				println("     */");
			}
			println("    public  " + _Escaper + " templateEscaper() {");
			println("        return this.escaper;");
			println("    }");
			println("");
		}
		if (GeneratedMethod.templateFormatter.gen(generatedMethods)) {
			if (jstachio)
				println("    @Override");
			else {
				println("    /**");
				println("     * Current formatter.");
				println("     * @return formatter");
				println("     */");
			}
			println("    public " + _Formatter + " templateFormatter() {");
			println("        return this.formatter;");
			println("    }");
			println("");
		}
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
			println("    /**");
			println("     * Needed for jstachio runtime.");
			println("     * @hidden");
			println("     */");
			println("    @SuppressWarnings(\"unchecked\")");
			println("    @Override");
			println("    public void process(Object model, Appendable appendable) throws java.io.IOException {");
			println("        execute( (" + className + ") model, appendable);");
			println("    }");
			println("");
			println("    /**");
			println("     * Needed for jstachio runtime.");
			println("     * @hidden");
			println("     */");
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
		writeExtendsConstructors(extendsElement, rendererClassSimpleName);
		writeRendererDefinitionMethod(TemplateCompilerType.SIMPLE, model);
		println("}");
	}

	private void writeExtendsConstructors(@Nullable TypeElement extendsElement, String rendererClassSimpleName) {
		if (extendsElement == null) {
			return;
		}
		List<ExecutableElement> constructors = ElementFilter.constructorsIn(extendsElement.getEnclosedElements())
				.stream().filter(e -> e.getModifiers().contains(Modifier.PUBLIC)
						&& !e.getModifiers().contains(Modifier.FINAL) && !e.getParameters().isEmpty())
				.toList();
		for (var c : constructors) {
			writeConstructor(rendererClassSimpleName, c);
		}
	}

	private void writeConstructor(String rendererClassSimpleName, ExecutableElement c) {
		StringBuilder sig = new StringBuilder();
		StringBuilder _super = new StringBuilder();

		sig.append("public ").append(rendererClassSimpleName).append("(");
		_super.append("super(");
		boolean first = true;
		for (var p : c.getParameters()) {
			if (!first) {
				sig.append(", ");
				_super.append(", ");
			}
			else {
				first = false;
			}
			for (var anno : p.getAnnotationMirrors()) {
				var targets = annotationTargets(anno);
				if (targets.contains(ElementType.PARAMETER)) {
					sig.append(anno.toString()).append(" ");
				}
			}
			sig.append(ToStringTypeVisitor.toCodeSafeString(p.asType()));
			sig.append(" ");
			sig.append(p.getSimpleName());
			_super.append(p.getSimpleName());
		}
		sig.append(")");
		_super.append(");");

		println("");
		for (var anno : c.getAnnotationMirrors()) {
			println("    " + anno);
		}
		println("    " + sig.toString() + " {");
		println("        " + _super.toString());
		println("        " + "this.formatter = __formatter(null);");
		println("        " + "this.escaper = __escaper(null);");
		println("    }");
		println("");
	}

	EnumSet<ElementType> annotationTargets(AnnotationMirror anno) {

		System.out.println(anno);
		Target target = anno.getAnnotationType().getAnnotation(Target.class);
		System.out.println(target);
		if (target == null) {
			return EnumSet.allOf(ElementType.class);
		}
		ElementType[] ets = target.value();
		if (ets == null) {
			return EnumSet.allOf(ElementType.class);
		}
		EnumSet<ElementType> t = EnumSet.noneOf(ElementType.class);
		for (var et : ets) {
			t.add(et);
		}
		if (t.isEmpty()) {
			/*
			 * Per the Target javadoc I think
			 */
			t = EnumSet.allOf(ElementType.class);
		}
		return t;
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