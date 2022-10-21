package io.jstach.apt;

import io.jstach.apt.TemplateCompilerLike.TemplateLoader;

public interface TemplateCompilerSupport {

	public TemplateLoader getTemplateLoader();

	public CodeAppendable getWriter();

}
