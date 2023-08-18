package io.jstach.apt.internal.context;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.LoggingSupport;
import io.jstach.apt.internal.LoggingSupport.MessagerLogging;
import io.jstach.apt.internal.NamedTemplate;
import io.jstach.apt.internal.util.ClassRef;
import io.jstach.apt.prism.Prisms.Flag;

public sealed interface TemplateStack extends MessagerLogging {

	public String getTemplateName();

	public ClassRef getModelClass();

	public @Nullable TemplateStack getCaller();

	public TemplateType getTemplateType();

	public AnnotationMirror annotationToLog();

	public enum TemplateType {

		ROOT, PARTIAL, PARAMETER_PARTIAL, LAMBDA

	}

	default String describeTemplateStack() {
		StringBuilder sb = new StringBuilder();
		sb.append(getTemplateName());
		@Nullable
		TemplateStack parent = getCaller();
		while (parent != null) {
			sb.append(" <- ");
			sb.append(parent.getTemplateName());
			parent = parent.getCaller();
		}

		return sb.toString();
	}

	default StringBuilder logName(StringBuilder sb) {
		var it = stack().descendingIterator();
		sb.append(getModelClass().getSimpleName());
		while (it.hasNext()) {
			var s = it.next();
			switch (s.getTemplateType()) {
				case PARTIAL -> sb.append(" >");
				case PARAMETER_PARTIAL -> sb.append(" <");
				case ROOT -> sb.append(" = ");
				case LAMBDA -> sb.append("#");
			}
			sb.append(s.getTemplateName());
		}
		return sb;
	}

	default Deque<TemplateStack> stack() {
		ArrayDeque<TemplateStack> stack = new ArrayDeque<>();
		stack.add(this);
		@Nullable
		TemplateStack parent = getCaller();
		while (parent != null) {
			stack.add(parent);
			parent = parent.getCaller();
		}

		return stack;
	}

	default TemplateStack ofParameterPartial(String templateName) {
		return new SimpleTemplateStack(templateName, this, TemplateType.PARAMETER_PARTIAL);
	}

	default TemplateStack ofPartial(String templateName) {
		return new SimpleTemplateStack(templateName, this, TemplateType.PARTIAL);
	}

	default TemplateStack ofLambda(String templateName) {
		return new SimpleTemplateStack(templateName, this, TemplateType.LAMBDA);
	}

	public static TemplateStack ofRoot(ClassRef modelClass, NamedTemplate template, Set<Flag> flags) {
		return new RootTemplateStack(modelClass, template, flags);
	}

	@Override
	default Messager messager() {
		return JavaLanguageModel.getInstance().getMessager();
	}

	@Override
	default void debug(CharSequence message) {
		if (isDebug()) {
			var out = outWriter();
			if (out != null) {
				StringBuilder sb = new StringBuilder("[JSTACHIO] ");
				logName(sb).append(": ").append(message);
				out.println(sb.toString());
			}
		}
	}

	@Override
	default void error(CharSequence message, Throwable t) {
		printError(message + " " + t.getMessage());
		var out = errorWriter();
		if (out != null) {
			StringBuilder sb = new StringBuilder("[JSTACHIO] ");
			logName(sb).append(": ").append(message);
			out.println(sb.toString());
			t.printStackTrace(out);
		}
	}

	@Override
	default boolean isDebug() {
		return flags().contains(Flag.DEBUG);
	}

	default LoggingSupport logging() {
		return this;
	}

	record SimpleTemplateStack(String templateName, TemplateStack caller, TemplateType type) implements TemplateStack {

		@Override
		public String getTemplateName() {
			return templateName;
		}

		@Override
		public @Nullable TemplateStack getCaller() {
			return caller;
		}

		@Override
		public ClassRef getModelClass() {
			return caller().getModelClass();
		}

		@Override
		public TemplateType getTemplateType() {
			return type();
		}

		@Override
		public AnnotationMirror annotationToLog() {
			return caller.annotationToLog();
		}

		@Override
		public Element elementToLog() {
			return caller.elementToLog();
		}

	}

	record RootTemplateStack(ClassRef modelClass, //
			NamedTemplate template, //
			Set<Flag> flags) implements TemplateStack {

		@Override
		public String getTemplateName() {
			return template.name();
		}

		@Override
		public @Nullable TemplateStack getCaller() {
			return null;
		}

		@Override
		public ClassRef getModelClass() {
			return modelClass();
		}

		@Override
		public TemplateType getTemplateType() {
			return TemplateType.ROOT;
		}

		@Override
		public AnnotationMirror annotationToLog() {
			return template.annotationMirror();
		}

		@Override
		public Element elementToLog() {
			return template.element();
		}

		@Override
		public Set<Flag> flags() {
			return this.flags;
		}

		public NamedTemplate template() {
			return this.template;
		}

	}

	default Set<Flag> flags() {
		var caller = getCaller();
		if (caller != null) {
			return caller.flags();
		}
		return Set.of();
	}

}
