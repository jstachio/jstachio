package io.jstach.opt.jmustache;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.TemplateLoader;
import com.samskivert.mustache.Template;

import io.jstach.jstache.JStacheLambda;
import io.jstach.jstache.JStachePartial;
import io.jstach.jstache.JStachePartials;
import io.jstach.jstachio.TemplateInfo;
import io.jstach.jstachio.spi.Templates;
import io.jstach.jstachio.spi.Templates.PathInfo;

class CompilerAdapter {

	private final Mustache.Compiler compiler;

	private final Loader loader;

	private final String prefix;

	private final String suffix;

	private final Map<String, JStachePartial> partials;

	private String sectionBody = null;

	public CompilerAdapter(TemplateInfo template, Class<?> modelClass, Loader loader) {
		this.compiler = Mustache.compiler() //
				.standardsMode(false) //
				.withEscaper(template.templateEscaper()::apply) //
				.withFormatter(template.templateFormatter()::apply) //
				.withLoader(new JStachioTemplateLoader()) //
				.withCollector(new JStachioCollector(this));

		PathInfo pathInfo = Templates.getPathInfo(modelClass);

		this.loader = loader;
		Map<String, JStachePartial> ps = new LinkedHashMap<>();
		JStachePartials jps = modelClass.getAnnotation(JStachePartials.class);
		if (jps != null) {
			for (var partial : jps.value()) {
				ps.put(partial.name(), partial);
			}
		}
		this.partials = Map.copyOf(ps);
		this.prefix = pathInfo.prefix();
		this.suffix = pathInfo.suffix();
	}

	private class JStachioTemplateLoader implements TemplateLoader {

		@Override
		public Reader getTemplate(String name) throws Exception {
			if (sectionBody != null && JStacheLambda.SECTION_PARTIAL_NAME.equals(name)) {
				return new StringReader(sectionBody);
			}
			var partial = partials.get(name);
			if (partial != null) {
				return openPartial(partial);
			}
			return openPartialByPath(name);
		}

	}

	Reader openPartial(JStachePartial partial) throws IOException {
		if (!partial.path().isEmpty()) {
			return openPartialByPath(partial.path());
		}
		return new StringReader(partial.template());
	}

	Reader openPartialByPath(String templatePath) throws IOException {
		String fullPath = prefix + templatePath + suffix;
		return loader.openPartial(fullPath);
	}

	Template compileForLambda(String template, String sectionBody) {
		this.sectionBody = sectionBody;
		return this.compiler.compile(template);
	}

	public Template compile(Reader br) {
		return this.compiler.compile(br);
	}

}
