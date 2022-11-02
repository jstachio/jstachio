package io.jstach.jmustache;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.kohsuke.MetaInfServices;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import io.jstach.RenderFunction;
import io.jstach.TemplateInfo;
import io.jstach.spi.JStacheConfig;
import io.jstach.spi.JStacheServices;

@MetaInfServices(JStacheServices.class)
public class JMustacheRenderer implements JStacheServices {

	private final AtomicBoolean use;

	private volatile @Nullable String prefix = null;

	private volatile @Nullable String suffix = null;

	public JMustacheRenderer use(boolean flag) {
		use.set(flag);
		log(flag);
		return this;
	}

	public JMustacheRenderer prefix(@Nullable String prefix) {
		this.prefix = prefix;
		return this;
	}

	public JMustacheRenderer suffix(@Nullable String suffix) {
		this.suffix = suffix;
		return this;
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
		use = new AtomicBoolean();
	}

	@Override
	public void init(JStacheConfig config) {
		use(config.getBoolean("jstachio.jmustache"));
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
	public RenderFunction filter(TemplateInfo template, Object context, RenderFunction previous) {
		if (!use.get()) {
			return previous;
		}
		return (a) -> {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			switch (template.templateSource()) {
				case STRING -> {
					Template t = createCompiler(template).compile(template.templateString());
					String results = t.execute(context);
					if (prefix != null) {
						a.append(prefix);
					}
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
