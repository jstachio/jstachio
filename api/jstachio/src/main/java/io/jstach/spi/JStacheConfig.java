package io.jstach.spi;

import java.lang.System.Logger;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public interface JStacheConfig {

	public @Nullable String getProperty(String key);

	default boolean getBoolean(String key) {
		String prop = getProperty(key);
		return Boolean.valueOf(prop);
	}

	public static String REFLECTION_TEMPLATE_LOOKUP = "jstachio.reflection.template";

	public static String USE_SYSTEM_LOGGER = "jstachio.logging";

	default Logger getLogger(String name) {
		if (getBoolean(USE_SYSTEM_LOGGER)) {
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
