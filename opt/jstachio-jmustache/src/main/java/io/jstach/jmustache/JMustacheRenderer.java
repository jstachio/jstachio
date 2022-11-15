package io.jstach.jmustache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jdt.annotation.Nullable;
import org.kohsuke.MetaInfServices;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import io.jstach.JStachio;
import io.jstach.TemplateInfo;
import io.jstach.spi.AbstractJStacheEngine;
import io.jstach.spi.JStacheConfig;
import io.jstach.spi.JStacheServices;

/**
 * Use JMustache instead of JStachio for rendering. The idea of this extension is to allow
 * you to edit Mustache templates in real time without waiting for the compile reload
 * cycle.
 * <p>
 * If this extension is enabled which it is by default if the ServiceLoader finds it
 * JMustache will be used when a runtime filtered rendering call is made (see
 * {@link JStachio}).
 * <p>
 * <strong>Strongly recommended you disable this in production via
 * {@link #JSTACHIO_JMUSTACHE_DISABLE} or {@link #use}</strong>
 *
 * @author agentgt
 * @see JStachio
 */
@MetaInfServices(JStacheServices.class)
public class JMustacheRenderer extends AbstractJStacheEngine {

	/**
	 * Property key of where jmustache will try to load template files. Default is
	 * <code>src/main/resources</code>.
	 */
	public static final String JSTACHIO_JMUSTACHE_SOURCE_PATH = "jstachio.jmustache.source";

	/**
	 * Property key to disable jmustache. Default is <code>false</code>.
	 */
	public static final String JSTACHIO_JMUSTACHE_DISABLE = "jstachio.jmustache.disable";

	private final AtomicBoolean use;

	private volatile @Nullable String prefix = null;

	private volatile @Nullable String suffix = null;

	private String sourcePath = "src/main/resources";

	private Logger logger = JStacheConfig.noopLogger();

	private long initTime = System.currentTimeMillis();

	/**
	 * Enables JMustache
	 * @param flag true enables
	 * @return return this for builder like config
	 */
	public JMustacheRenderer use(boolean flag) {
		use.set(flag);
		log(flag);
		return this;
	}

	/**
	 * A prefix to add to the output to know that JMustache is being used.
	 * @param prefix string to prefix output
	 * @return return this for builder like config
	 */
	public JMustacheRenderer prefix(@Nullable String prefix) {
		this.prefix = prefix;
		return this;
	}

	/**
	 * A suffix to append to the output to know that JMustache is being used.
	 * @param suffix string to suffix output
	 * @return return this for builder like config
	 */
	public JMustacheRenderer suffix(@Nullable String suffix) {
		this.suffix = suffix;
		return this;
	}

	/**
	 * Sets the relative to the project sourcePath for runtime lookup of templates. By
	 * default is <code>src/main/resources</code>.
	 * @param sourcePath by default is <code>src/main/resources</code>
	 * @return sourcePath should not be null
	 */
	public JMustacheRenderer sourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
		return this;
	}

	protected void log(boolean flag) {
		logger.log(Level.INFO,
				"JMustache is now: " + (flag ? "enabled" : "disabled") + " using sourcePath: " + sourcePath);
	}

	protected void log(TemplateInfo template) {
		if (logger.isLoggable(Level.DEBUG)) {
			logger.log(Level.DEBUG, "Using JMustache. template: " + template.description());
		}
	}

	/**
	 * No-arg constructor for ServiceLoader
	 */
	public JMustacheRenderer() {
		use = new AtomicBoolean();
	}

	@Override
	public void init(JStacheConfig config) {
		logger = config.getLogger(getClass().getCanonicalName());
		sourcePath(config.requireProperty(JSTACHIO_JMUSTACHE_SOURCE_PATH, sourcePath));
		use(!config.getBoolean(JSTACHIO_JMUSTACHE_DISABLE));

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
	protected boolean execute(Object context, Appendable a, TemplateInfo template, boolean broken) throws IOException {
		if (!use.get()) {
			return false;
		}

		switch (template.templateSource()) {
			case STRING -> {
				Template t = createCompiler(template).compile(template.templateString());
				String results = t.execute(context);
				if (prefix != null) {
					a.append(prefix);
				}
				a.append(results);
				return true;
			}
			case RESOURCE -> {
				String templatePath = template.templatePath();
				var path = Path.of(sourcePath, templatePath);
				InputStream stream;
				boolean _broken = template.lastLoaded() > 0 || broken;
				if ((_broken && path.toFile().isFile()) || path.toFile().lastModified() > initTime) {
					stream = openFile(path);
				}
				else if (broken) {
					stream = openResource(templatePath);
				}
				else {
					return false;
				}
				try (InputStream is = stream;
						BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
					Template t = createCompiler(template).compile(br);
					String results = t.execute(context);
					a.append(results);
					return true;
				}
			}
		}
		return false;
	}

	protected InputStream openFile(Path path) throws IOException {
		InputStream is = Files.newInputStream(path);
		if (logger.isLoggable(Level.DEBUG)) {
			logger.log(Level.DEBUG, "Using JMustache. template:" + "file " + path);
		}
		return is;
	}

	protected InputStream openResource(String templatePath) throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream is = loader.getResourceAsStream(templatePath);
		if (is == null) {
			throw new IOException("template not found. template: " + templatePath);
		}
		if (logger.isLoggable(Level.DEBUG)) {
			logger.log(Level.DEBUG, "Using JMustache. template:" + "classpath " + templatePath);
		}
		return is;
	}

}
