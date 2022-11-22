package io.jstach.jstachio.spi;

import java.lang.System.Logger;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Runtime Config Service.
 * <p>
 * The default config service uses System properties but can be extended through
 * {@link JStachioServices#provideConfig()}.
 * <p>
 * While a majority of jstachio config is static and done at compile time some config like
 * logging and disabling extensions is needed at runtime. Config and DI agnostic
 * extensions should use this facility for simple key valuy based config.
 *
 * @see JStachioServices
 * @author agentgt
 */
public interface JStachioConfig {

	/**
	 * Config key to disable reflection based lookup of templates for other fallback
	 * mechanisms
	 */
	public static String REFLECTION_TEMPLATE_DISABLE = "jstachio.reflection.template.disable";

	/**
	 * Config key to disable if logging. By default logging is enabled.
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
	 * Extensions might find this useful to set a nonnull Logger field like: <pre>
	 * private Logger logger = JStacheConfig.noopLogger();
	 * public void init(JStacheConfig config) {
	 *     logger = config.getLogger(getClass().getName());
	 * }
	 * </pre>
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
