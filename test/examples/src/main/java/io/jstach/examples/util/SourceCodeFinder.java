package io.jstach.examples.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import io.jstach.jstachio.spi.Templates;

public final class SourceCodeFinder {

	private SourceCodeFinder() {

	}

	public static Path codePath(Class<?> templateClass) {
		try {
			String generatedClassName = Templates.generatedClassName(templateClass);
			String sourcePath = generatedClassName.replace(".", "/") + ".java";
			return Path.of(sourcePath);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String code(Path sourcePath, Class<?> templateClass) throws IOException {
		var codePath = codePath(templateClass);
		Path resolved = sourcePath.resolve(codePath);
		return Files.readString(resolved, StandardCharsets.UTF_8);
	}

}
