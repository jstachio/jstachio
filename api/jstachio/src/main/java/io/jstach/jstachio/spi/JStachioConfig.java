package io.jstach.jstachio.spi;

import java.lang.System.Logger;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstache.JStacheConfig;

/**
 * Runtime Config Service.
 * <p>
 * While a majority of jstachio config is static and done at compile time some config like
 * logging and disabling extensions is needed at runtime. Config and DI agnostic
 * extensions should use this facility for simple key value based config.
 * <p>
 * The default resolved config uses System properties but can be replaced by implementing
 * this extension.
 * <p>
 * Core runtime configuration properties for the {@link JStachioFactory#defaultJStachio()
 * default JStachio} are:
 * <ul id="_jstachio_config_properties">
 * <li>{@link #REFLECTION_TEMPLATE_DISABLE}</li>
 * <li>{@link #SERVICELOADER_TEMPLATE_DISABLE}</li>
 * <li>{@link #LOGGING_DISABLE}</li>
 * </ul>
 * <strong>This configuration is for runtime only and {@link JStacheConfig not static
 * configuration} needed for code generation.</strong>
 *
 * @see JStachioExtension
 * @author agentgt
 */
public non-sealed interface JStachioConfig extends JStachioExtension {

	/**
	 * Config key to disable non service loader reflection based lookup of templates. If a
	 * custom JStachio is being used this configuration property maybe irrelevant.
	 * <p>
	 * Valid values are <code>true</code> or <code>false</code>. The default is
	 * <code>false</code>.
	 */
	public static String REFLECTION_TEMPLATE_DISABLE = "jstachio.reflection.template.disable";

	/**
	 * Config key to disable service loader based lookup of templates. If a custom
	 * JStachio is being used this configuration property maybe irrelevant.
	 * <p>
	 * Valid values are <code>true</code> or <code>false</code>. The default is
	 * <code>false</code>.
	 */
	public static String SERVICELOADER_TEMPLATE_DISABLE = "jstachio.serviceloader.template.disable";

	/**
	 * Config key to disable logging. By default logging is enabled and will use the
	 * {@link System.Logger}. If a custom {@link JStachioConfig} is being used this
	 * configuration property maybe irrelevant.
	 * <p>
	 * Valid values are <code>true</code> or <code>false</code>. The default is
	 * <code>false</code>.
	 */
	public static String LOGGING_DISABLE = "jstachio.logging.disable";

	/**
	 * Gets a property from some config implementation.
	 * @param key the key to use to lookup
	 * @return if not found <code>null</code>.
	 */
	public @Nullable String getProperty(String key);

	/**
	 * See {@link Boolean#getBoolean(String)}.
	 * @param key the property key
	 * @return only true if string is "true"
	 */
	default boolean getBoolean(String key) {
		String prop = getProperty(key);
		return Boolean.parseBoolean(prop);
	}

	/**
	 * Gets the property as a boolean and if no property value is found the fallback is
	 * used.
	 * @param key property key
	 * @param fallback if property has no value this value is used.
	 * @return the parsed boolean or the fallback
	 */
	default boolean getBoolean(String key, boolean fallback) {
		String prop = getProperty(key);
		if (prop == null) {
			return fallback;
		}
		return Boolean.parseBoolean(prop);
	}

	/**
	 * A NonNull friendly analog of {@link System#getProperty(String, String)} that will
	 * never return null unlike System.getProperty which is PolyNull.
	 * @param key checked if null and will NPE immediatly if it is
	 * @param fallback used if the retrieved property is null
	 * @return property or fallback if property is not found (<code>null</code>).
	 * @throws NullPointerException if the fallback is null or if the key is null.
	 */
	default String requireProperty(String key, String fallback) {
		if (key == null) {
			throw new NullPointerException("key is null");
		}
		String v = getProperty(key);
		if (v == null) {
			v = fallback;
		}
		if (v == null) {
			throw new NullPointerException("fallback is null. key: " + key);
		}
		return v;
	}

	/**
	 * Gets a system logger if the property {@link #LOGGING_DISABLE} is
	 * <code>false</code>. If the property is set to a <code>true</code> value a NOOP
	 * Logger <em>that will not trigger initialization of the System {@link Logger}
	 * facilities</em> will be returned. The NOOP logger is always disabled at every level
	 * and will not produce any output.
	 * @param name the name of the logger usually the class.
	 * @return the System logger.
	 * @see #noopLogger()
	 */
	default Logger getLogger(String name) {
		if (!getBoolean(LOGGING_DISABLE)) {
			return System.getLogger(name);
		}
		return noopLogger();
	}

	/**
	 * NOOP Logger <em>that will not trigger initialization of the System {@link Logger}
	 * facilities</em>. The NOOP logger is always disabled at every level and will not
	 * produce any output.
	 * <p>
	 * Extensions might find this useful to set a nonnull Logger field like:
	 * <pre><code class="language-java">
	 * private Logger logger = JStacheConfig.noopLogger();
	 * public void init(JStacheConfig config) {
	 *     logger = config.getLogger(getClass().getName());
	 * }
	 * </code> </pre>
	 * @return singleton instance of noop logger
	 */
	public static Logger noopLogger() {
		return NOOPLogger.INSTANCE;
	}

}

enum NOOPLogger implements Logger {

	INSTANCE;

	@Override
	public @NonNull String getName() {
		return "NOOPLogger";
	}

	@Override
	public boolean isLoggable(@NonNull Level level) {
		return false;
	}

	@Override
	public void log(@NonNull Level level, @Nullable ResourceBundle bundle, @Nullable String msg,
			@Nullable Throwable thrown) {

	}

	@Override
	public void log(@NonNull Level level, @Nullable ResourceBundle bundle, @Nullable String format,
			@Nullable Object @NonNull... params) {

	}

}

enum SystemPropertyConfig implements JStachioConfig {

	INSTANCE;

	@Override
	public @Nullable String getProperty(String key) {
		return System.getProperty(key);
	}

}

class CompositeConfig implements JStachioConfig {

	private final List<JStachioConfig> configs;

	CompositeConfig(List<JStachioConfig> configs) {
		super();
		this.configs = configs;
	}

	@Override
	public @Nullable String getProperty(String key) {
		for (var c : configs) {
			String v = c.getProperty(key);
			if (v != null) {
				return v;
			}
		}
		return null;
	}

}
