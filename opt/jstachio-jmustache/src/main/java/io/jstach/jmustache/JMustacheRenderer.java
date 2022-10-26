package io.jstach.jmustache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jdt.annotation.NonNull;
import org.kohsuke.MetaInfServices;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import io.jstach.RenderFunction;
import io.jstach.TemplateInfo;
import io.jstach.spi.JStacheServices;

@MetaInfServices(JStacheServices.class)
public class JMustacheRenderer implements JStacheServices {

	private final AtomicBoolean use;

	public void use(boolean flag) {
		use.set(flag);
		log(flag);
	}

	protected void log(boolean flag) {
		@NonNull
		Logger logger = System.getLogger(getClass().getCanonicalName());
		logger.log(Level.INFO, "JMustache is now: " + (flag ? "enabled" : "disabled"));
	}

	protected void log(TemplateInfo template) {
		Logger logger = System.getLogger(getClass().getCanonicalName());
		if (logger.isLoggable(Level.DEBUG)) {
			logger.log(Level.DEBUG, "Using JMustache. template: " + template.description());
		}
	}

	public JMustacheRenderer() {
		use = new AtomicBoolean(Boolean.getBoolean("jstachio.jmustache"));
		log(use.get());
	}

	protected Mustache.Compiler createCompiler(TemplateInfo template) {
		var compiler = Mustache.compiler() //
				.standardsMode(false) //
				.withEscaper(template.templateEscaper()::apply) //
				.withFormatter(template.templateFormatter()::apply) //
				.withCollector(new JStachioCollector());
		return compiler;
	}

	@Override
	public RenderFunction renderer(TemplateInfo template, Object context, RenderFunction previous) throws IOException {
		if (!use.get()) {
			return previous;
		}
		return (a) -> {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			switch (template.templateSource()) {
				case STRING -> {
					Template t = createCompiler(template).compile(template.templateString());
					String results = t.execute(context);
					a.append(results);
				}
				case RESOURCE -> {
					try (InputStream is = loader.getResourceAsStream(template.templatePath());
							BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
						Template t = createCompiler(template).compile(br);
						String results = t.execute(context);
						a.append(results);
					}
				}
			}
		};
	}

}
