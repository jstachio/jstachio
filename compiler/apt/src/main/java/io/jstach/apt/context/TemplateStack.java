package io.jstach.apt.context;

import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.LoggingSupport;
import io.jstach.apt.NamedTemplate;
import io.jstach.apt.prism.Prisms.Flag;

public sealed interface TemplateStack extends LoggingSupport {

	public String getTemplateName();

	public @Nullable TemplateStack getCaller();

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

	default TemplateStack ofPartial(String templateName) {
		return new SimpleTemplateStack(templateName, this);
	}

	default TemplateStack ofLambda(String templateName) {
		return new SimpleTemplateStack(templateName, this);
	}

	public static TemplateStack ofRoot(NamedTemplate template, Set<Flag> flags) {
		return new RootTemplateStack(template, flags);
	}

	default void debug(CharSequence message) {
		if (isDebug()) {
			var out = System.out;
			if (out != null) {
				out.println("[JSTACHIO] " + getTemplateName() + ": " + message);
			}
		}
	}

	default boolean isDebug() {
		return flags().contains(Flag.DEBUG);
	}

	record SimpleTemplateStack(String templateName, @Nullable TemplateStack caller) implements TemplateStack {

		public String getTemplateName() {
			return templateName;
		}

		public @Nullable TemplateStack getCaller() {
			return caller;
		}
	}

	record RootTemplateStack(NamedTemplate template, Set<Flag> flags) implements TemplateStack {

		public String getTemplateName() {
			return template.name();
		}

		public @Nullable TemplateStack getCaller() {
			return null;
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
