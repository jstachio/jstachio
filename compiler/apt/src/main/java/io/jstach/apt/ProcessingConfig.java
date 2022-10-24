package io.jstach.apt;

import java.nio.charset.Charset;
import java.util.Set;

import io.jstach.annotation.JStacheFlags.Flag;

public interface ProcessingConfig extends LoggingSupport {

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

	default String resourcesPath() {
		return "src/main/resources";
	}

	default boolean fallbackToFilesystem() {
		return true;
	}

	default boolean isGradle() {
		String cmd = System.getProperty("sun.java.command");
		if (cmd != null) {
			return cmd.toLowerCase().contains("gradle");
		}
		return false;
	}

}
