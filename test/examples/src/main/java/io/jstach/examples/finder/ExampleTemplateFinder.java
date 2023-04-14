package io.jstach.examples.finder;

import java.util.List;

import io.jstach.examples.delimiter.DelimiterExampleRenderer;
import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateConfig;
import io.jstach.jstachio.TemplateInfo;
import io.jstach.jstachio.spi.TemplateProvider;

public class ExampleTemplateFinder implements TemplateProvider.GeneratedTemplateProvider {

	@Override
	public List<Template<?>> provideTemplates(TemplateConfig templateConfig) {
		DelimiterExampleRenderer r = new DelimiterExampleRenderer(templateConfig);
		return List.of(r);
	}

	@Override
	public Iterable<? extends TemplateInfo> templates() {
		return provideTemplates();
	}

}
