package io.jstach;

public interface TemplateInfo {

	public String templateName();

	public String templatePath();

	default String templateString() {
		return "";
	}

	default TemplateSource templateSource() {
		return templatePath().isEmpty() ? TemplateSource.STRING : TemplateSource.RESOURCE;
	}

	public enum TemplateSource {

		RESOURCE, STRING

	}

}
