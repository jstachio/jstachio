package io.jstach.spi;

import java.lang.System.Logger;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Config Service.
 * <p>
 * The default config service uses System properties.
 *
 * @author agentgt
 */
public interface JStacheConfig {

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
	 * Config key
	 */
	public static String REFLECTION_TEMPLATE_LOOKUP = "jstachio.reflection.template";

	/**
	 * Config key
	 */
	public static String USE_SYSTEM_LOGGER = "jstachio.logging";

	/**
	 * Gets a system logger if the property {@link #USE_SYSTEM_LOGGER} is set.
	 * @param name the name of the logger usually the class.
	 * @return the System logger.
	 */
	default Logger getLogger(String name) {
		if (getBoolean(USE_SYSTEM_LOGGER, true)) {
			return System.getLogger(name);
		}
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

enum SystemPropertyConfig implements JStacheConfig {

	INSTANCE;

	@Override
	public @Nullable String getProperty(String key) {
		return System.getProperty(key);
	}

}

class CompositeConfig implements JStacheConfig {

	private final List<JStacheConfig> configs;

	CompositeConfig(List<JStacheConfig> configs) {
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
