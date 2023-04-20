package io.jstach.opt.jmustache;

import java.io.IOException;
import java.io.Reader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jdt.annotation.Nullable;
import org.kohsuke.MetaInfServices;

import com.samskivert.mustache.Template;

import io.jstach.jstache.JStacheLambda;
import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.TemplateInfo;
import io.jstach.jstachio.spi.JStachioConfig;
import io.jstach.jstachio.spi.JStachioExtension;
import io.jstach.jstachio.spi.Templates;

/**
 * Use JMustache instead of JStachio for rendering. The idea of this extension is to allow
 * you to edit Mustache templates in real time without waiting for the compile reload
 * cycle.
 * <p>
 * You are probably asking yourself <em>why do I need JMustache if I have JStachio</em>?
 * Unfortunately JStachio needs the annotation processor to run <em>every time a template
 * is changed!</em>. While there are incremental compilers like Eclipse that do support
 * incrementally compiling annotations they are often not triggered via editing resources.
 * Furthermore incremental compilation often just doesn't work.
 * <p>
 * Enter JMustache. Through reflection you can edit your templates while an application is
 * running. Luckily <em>JMustache and JStachio are almost entirely compatible especially
 * through this extension</em> which configures JMustache to act like JStachio. Even
 * {@link JStacheLambda} will work.
 * <p>
 * <strong>The only major compatibility issue is that JMustache currently does not support
 * mustache inheritance (parents and blocks)!</strong>
 * <p>
 * If this extension is enabled which it is by default if the ServiceLoader finds it
 * JMustache will be used when a runtime filtered rendering call is made (see
 * {@link io.jstach.jstachio.JStachio}).
 * <p>
 * How this works is this extension is a filter that checks to see if the statically
 * generated renderer (template) can render and that its template is up-to-date. If it is
 * not then JMustache will use the template meta data to construct its own template and
 * then execute it. In some cases the annotation processor does not even have to run for
 * this to work (see {@link Templates#getInfoByReflection(Class)}.
 * <p>
 * <strong>Strongly recommended you disable this in production via
 * {@link #JSTACHIO_JMUSTACHE_DISABLE} or {@link #use}</strong>
 *
 * @author agentgt
 * @see JStachio
 */
@MetaInfServices(JStachioExtension.class)
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

	private Logger logger = JStachioConfig.noopLogger();

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
	public void init(JStachioConfig config) {
		logger = config.getLogger(getClass().getCanonicalName());
		sourcePath(config.requireProperty(JSTACHIO_JMUSTACHE_SOURCE_PATH, sourcePath));
		use(!config.getBoolean(JSTACHIO_JMUSTACHE_DISABLE));

	}

	protected CompilerAdapter createCompiler(TemplateInfo template, Class<?> modelClass) {
		Loader loader = new Loader(logger, sourcePath, initTime);
		return new CompilerAdapter(template, modelClass, loader);
	}

	@Override
	protected boolean execute(Object context, Appendable a, TemplateInfo template, boolean broken) throws IOException {
		if (!use.get()) {
			return false;
		}

		Loader loader = new Loader(logger, sourcePath, initTime);

		Reader reader = loader.open(template, broken);

		if (reader != null) {
			Template t = createCompiler(template, context.getClass()).compile(reader);
			String result = t.execute(context);
			if (prefix != null) {
				a.append(prefix);
			}
			a.append(result);
			if (suffix != null) {
				a.append(suffix);
			}
			return true;
		}
		return false;
	}

}
