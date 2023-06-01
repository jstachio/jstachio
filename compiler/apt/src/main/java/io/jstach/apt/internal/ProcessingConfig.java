package io.jstach.apt.internal;

import java.nio.charset.Charset;
import java.util.Set;

import io.jstach.apt.prism.Prisms.Flag;

public interface ProcessingConfig extends LoggingSupport.MessagerLogging {

	public Set<Flag> flags();

	Charset charset();

	@Override
	default boolean isDebug() {
		return flags().contains(Flag.DEBUG);
	}

	@Override
	default void debug(CharSequence message) {
		System.out.println("[JSTACHIO] " + message);

	}

	@Override
	default void error(CharSequence message, Throwable t) {
		printError(message);
		System.err.println("[JSTACHIO] " + message);
		t.printStackTrace(System.err);
	}

	default String resourcesPath() {
		return "src/main/resources";
	}

	default boolean fallbackToFilesystem() {
		return !resourcesPath().isBlank();
	}

	PathConfig pathConfig();

	default boolean isGradle() {
		String cmd = System.getProperty("sun.java.command");
		if (cmd != null) {
			return cmd.toLowerCase().contains("gradle");
		}
		return false;
	}

	public record PathConfig(String prefix, String suffix) {

		public String resolveTemplatePath(String path) {
			String templatePath = path;
			if (!templatePath.isBlank()) {
				templatePath = prefix() + templatePath + suffix();
			}
			return templatePath;
		}
	}

}
