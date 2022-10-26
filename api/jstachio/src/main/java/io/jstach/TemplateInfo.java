package io.jstach;

import java.util.function.Function;

public interface TemplateInfo {

	public String templateName();

	public String templatePath();

	default String templateString() {
		return "";
	}

	Function<String, String> templateEscaper();

	Function<Object, String> templateFormatter();

	default TemplateSource templateSource() {
		return templatePath().isEmpty() ? TemplateSource.STRING : TemplateSource.RESOURCE;
	}

	public enum TemplateSource {

		RESOURCE, STRING

	}

	default String description() {
		return String.format("TemplateInfo[%s, %s]", templateName(), templatePath());
	}

}
