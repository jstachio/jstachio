/*
 * Copyright (c) 2015, Victor Nazarov <asviraspossible@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation and/or
 *     other materials provided with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.jstach.apt;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import io.jstach.apt.internal.ProcessingConfig;

/**
 * @author Victor Nazarov
 * @author agentgt
 */
class TextFileObject {

	// private final FileObject resource;
	private final ProcessingEnvironment env;

	private final ProcessingConfig config;

	TextFileObject(ProcessingConfig config, ProcessingEnvironment env) {
		// this.resource = resource;
		this.env = env;
		this.config = config;
	}

	InputStream openInputStream(String name) throws IOException {

		/*
		 * Issue 61: the name cannot have a starting slash or else it fails so we need to
		 * normalize if it does.
		 */
		if (name.startsWith("/")) {
			if (config.isDebug()) {
				config.debug("Path starts with a starting slash. path=" + name);
			}
			name = name.substring(1);
		}

		/*
		 * Ideally we would use StandardLocation.SOURCE_PATH but that has issues with
		 * resources for Eclipse.
		 * https://stackoverflow.com/questions/22494596/eclipse-annotation-processor-get-
		 * project-path
		 */
		FileObject resource = env.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", name);
		if (resource.getLastModified() > 0) {
			return resource.openInputStream();
		}
		if (config.isDebug()) {
			config.debug("File not found with Filer. resource: " + resource.toUri());
		}
		/*
		 * Often times during incremental compilation via Eclipse or Gradle the resource
		 * is missing from the Filer. This is because it looks for the file in output
		 * directory and it has not been copied for whatever reason. So we go directly
		 * looking for the file.
		 */
		if (config.fallbackToFilesystem()) {
			/*
			 * We use a dummy FileObject to get a relative directory. This is because
			 * current work directory can be misleading depending on build implementation.
			 */
			FileObject dummy = env.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", OutputPathPattern.DUMMY);

			if (config.isGradleEnabled() && config.isDebug()) {
				config.debug("Looks like we are using Gradle incremental. dummy: " + dummy.toUri());

			}
			else if (config.isDebug()) {
				config.debug("Looks like we are using Eclipse or Intellij incremental. dummy: " + dummy.toUri());
			}

			/*
			 * Aka the CWD
			 */
			Path projectPath;

			OutputPathPattern pattern = OutputPathPattern.find(dummy.toUri());
			projectPath = pattern.resolveProjectPath(dummy.toUri());
			if (config.isDebug()) {
				config.debug("Detected class output pattern: " + pattern + " projectPath: " + projectPath);
			}

			var resourcePaths = config.resourcesPaths();
			if (resourcePaths.isEmpty()) {
				resourcePaths = pattern.relativeSourcePaths();
			}
			String resourceName = name;
			List<Path> fullPaths = resourcePaths.stream().map(rp -> resolvePath(projectPath, rp, resourceName))
					.toList();

			for (Path fullPath : fullPaths) {
				if (config.isDebug()) {
					config.debug("File not found with Filer. Trying direct file access. name:" + resourceName
							+ ", path: " + fullPath + ", dummy: " + dummy.toUri());
				}
				if (Files.isReadable(fullPath)) {
					return Files.newInputStream(fullPath);
				}
			}
			String error = printAttempts(new StringBuilder(), name, resource, fullPaths, "").toString();
			if (config.isDebug()) {
				StringBuilder diagnostic = new StringBuilder();
				printAttempts(diagnostic, name, resource, fullPaths, "\n\t");
				diagnostic.append("\n\n");
				diagnosticDump(diagnostic, config, Map.of("outputPathPattern", pattern.toString()));
				config.debug(diagnostic.toString());
			}
			throw new IOException(error);
		}

		return resource.openInputStream();
	}

	private static StringBuilder printAttempts(StringBuilder sb, String name, FileObject resource, List<Path> fullPaths,
			String separator) {
		sb.append("Failed to find template resource: '").append(name).append("'. Tried the following locations: ");
		sb.append(separator).append("'").append(resource.toUri()).append("'");
		for (Path p : fullPaths) {
			sb.append(separator).append(", '").append(p.toString()).append("'");
		}
		return sb;
	}

	private enum OutputPathPattern {

		GRADLE("/build/classes/java/main/", List.of("src/main/resources")), //
		GRADLE_TEST("/build/classes/java/test/", List.of("src/test/resources")), //
		MAVEN("/target/classes/", List.of("src/main/resources")), //
		MAVEN_TEST("/target/test-classes/", List.of("src/test/resources")), //
		CWD(".", List.of("src/main/resources")) {
			@Override
			public boolean matches(String uri) {
				return false;
			}

			@Override
			public Path resolveProjectPath(URI uri) {
				return Path.of(".");
			}
		};

		private static final String DUMMY = "dummy";

		private final String endPath;

		private final List<String> relativeSourcePaths;

		private OutputPathPattern(String endPath, List<String> relativeSourcePaths) {
			this.endPath = endPath;
			this.relativeSourcePaths = relativeSourcePaths;
		}

		public boolean matches(String uri) {
			return uri.endsWith(endPath + DUMMY);
		}

		public Path resolveProjectPath(URI uri) {
			Path path = Paths.get(uri);
			int segments = endPath.split("/").length;
			for (int i = 0; i < segments; i++) {
				var parent = path.getParent();
				if (parent == null) {
					throw new IllegalStateException("Path pattern (bug). pattern: " + this + " uri: " + uri);
				}
				path = parent;
			}
			return path;
		}

		public List<String> relativeSourcePaths() {
			return relativeSourcePaths;
		}

		public static OutputPathPattern find(URI uri) {
			String u = uri.toString().trim();
			for (var o : values()) {
				if (o.matches(u)) {
					return o;
				}
			}
			return CWD;
		}

	}

	private static StringBuilder diagnosticDump(StringBuilder sb, ProcessingConfig config, Map<String, String> keys) {
		sb.append("Environment Info:").append("\n");
		Map<String, String> info = new LinkedHashMap<>();
		info.putAll(keys);
		Path cwd = Path.of(".");
		try {
			info.put("CWD", cwd.toAbsolutePath().toString());
		}
		catch (Exception e) {
			info.put("CWD", cwd.toString());
		}
		info.put("JDK", Runtime.version().toString());
		info.put("isGradle", config.isGradleEnabled() + "");
		for (var e : info.entrySet()) {
			sb.append("\n\t").append(e.getKey()).append("=").append(e.getValue());
		}
		sb.append("\n\n");

		for (var e : System.getProperties().entrySet()) {
			var key = e.getKey().toString().toLowerCase(Locale.ENGLISH);
			/*
			 * Shitty heuristic for not printing out sensitive stuff
			 */
			if (key.contains("secret") || key.contains("password") || key.contains("key")
					|| key.contains("line.separator")) {
				continue;
			}
			sb.append("\n\t").append(e.getKey()).append("=").append(e.getValue());
		}
		return sb;
	}

	private static Path resolvePath(Path projectPath, String resourcePath, String name) {
		Path filePath = Path.of(resourcePath, name);
		return filePath.isAbsolute() ? filePath : projectPath.resolve(filePath);
	}

	Charset charset() {
		return config.charset();
	}

}
