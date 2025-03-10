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

import static java.util.Map.entry;

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
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.internal.ProcessingConfig;
import io.jstach.apt.internal.util.EclipseClasspath;
import io.jstach.apt.internal.util.EclipseClasspath.EclipseClasspathFile;
import io.jstach.apt.internal.util.Throwables;

/**
 * THERE BE MOTHER FUCKING DRAGONS HERE
 *
 * @author Victor Nazarov
 * @author agentgt
 */
class TextFileObject {

	private final ProcessingEnvironment env;

	private final ProcessingConfig config;

	private final static ConcurrentMap<Path, Optional<EclipseClasspathFile>> eclipseClasspathFileCache = new ConcurrentHashMap<>();

	TextFileObject(ProcessingConfig config, ProcessingEnvironment env) {
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
		boolean eclipseFileManager = env.getFiler().getClass().getName().startsWith("org.eclipse");
		boolean isGradle = config.isGradleEnabled();

		if (config.isDebug()) {
			config.debug("File not found with Filer. resource: " + resource.toUri());
			if (eclipseFileManager) {
				config.debug("Eclipse file manager is in use.");
			}
		}
		/*
		 * Often times during incremental compilation via Eclipse or Gradle the resource
		 * is missing from the Filer. This is because it looks for the file in output
		 * directory and it has not been copied for whatever reason. So we go directly
		 * looking for the file.
		 *
		 * The major thing we need to figure out is where is the source directory. This is
		 * challenging because with APT the CWD may not be the project directory
		 * furthermore there can be multiple source and output directories for a given
		 * project.
		 */
		if (config.fallbackToFilesystem()) {
			/*
			 * We use a dummy FileObject to get a relative directory. This is because
			 * current work directory can be misleading depending on build implementation.
			 */
			FileObject dummyClassOutput = env.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "",
					OutputPathPattern.DUMMY);
			FileObject dummySourceOutput = env.getFiler().getResource(StandardLocation.SOURCE_OUTPUT, "",
					OutputPathPattern.DUMMY);

			if (isGradle && config.isDebug()) {
				config.debug("Looks like we are using Gradle incremental. dummy: " + dummyClassOutput.toUri());

			}
			else if (config.isDebug()) {
				config.debug(
						"Looks like we are using Eclipse or Intellij incremental. dummy: " + dummyClassOutput.toUri());
			}

			OutputPathPattern outputPattern = OutputPathPattern.find(dummyClassOutput.toUri());
			/*
			 * Aka the CWD.
			 */
			Path projectPath;
			ProjectPattern pattern;
			if (outputPattern == OutputPathPattern.CWD && eclipseFileManager) {
				/*
				 * The following is because Eclipse projects that are not maven/gradle can
				 * have unusual source paths. Furthermore figuring out the CWD is not easy
				 * either.
				 *
				 * The EclipseClasspath will find the .classpath as well as parse it so
				 * that we can get the source paths.
				 *
				 * We do not do this for Eclipse and Gradle buildship because it is slow
				 * as shit parsing XML on every template.
				 */
				config.debug("Attempting to locate .classpath as we are in Eclipse");
				var classOutputPath = Path.of(dummyClassOutput.toUri()).getParent();
				if (classOutputPath == null) {
					throw new IOException(printAttempts("Failed to locate Eclipse .classpath! ", name, resource));
				}
				var classpathFile = findEclipseClasspathFile(classOutputPath);

				if (classpathFile == null) {
					config.debug("Failed to locate Eclipse .classpath!");
					throw new IOException(printAttempts("Failed to locate Eclipse .classpath! ", name, resource));
				}
				var projectDirectory = classpathFile.classpathFile().getParent();
				if (projectDirectory == null) {
					throw new IOException(printAttempts("No parent directory to Eclipse .classpath! ", name, resource));
				}
				projectPath = projectDirectory;
				var sourceOutputPath = Path.of(dummySourceOutput.toUri()).getParent();
				List<String> sourcePaths = classpathFile.findRelativeSourcePaths(classOutputPath, sourceOutputPath)
						.toList();
				pattern = new ProjectPattern.EclipseProjectPattern(sourcePaths);
				if (config.isDebug()) {
					config.info("Inside Eclipse. Using .classpath. name = " + name + " projectPath = " + projectPath
							+ " sourcePaths = " + sourcePaths + " resourcesPaths=" + config.resourcesPaths());
				}
			}
			else if (outputPattern == OutputPathPattern.CWD && isGradle) {
				String error = "We are in Gradle and the build output has been changed from the default! "
						+ "Please see: https://jstach.io/doc/jstachio/current/apidocs/#faq_template_not_found";
				var exception = new IOException(error);
				config.error(error, exception);
				throw exception;
			}
			else {
				if (outputPattern == OutputPathPattern.CWD) {
					config.warn("Using CWD to resolve templates directory. "
							+ "While this might work it is not reliable and suggested "
							+ "you use the jstache.resourcePaths compiler flag to the absolute path of your resources directory. "
							+ "Please see: https://jstach.io/doc/jstachio/current/apidocs/#faq_template_not_found");
				}
				projectPath = outputPattern.resolveProjectPath(dummyClassOutput.toUri());
				pattern = outputPattern;
			}
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
							+ ", path: " + fullPath + ", dummy: " + dummyClassOutput.toUri());
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
				List<Entry<String, String>> keys = List.of( //
						entry("projectPath", projectPath.toString()), //
						entry("classOutput", dummyClassOutput.toUri().toString()), //
						entry("sourceOutput", dummySourceOutput.toUri().toString()), //
						entry("outputPathPattern", pattern.toString()), //
						entry("jstache.resourcesPath", config.resourcesPaths().toString()), //
						entry("filer", env.getFiler().getClass().toString()) //
				);
				diagnosticDump(diagnostic, config, keys);
				config.warn(diagnostic.toString());
				if (diagnostic.length() > (4 * 1024)) {
					diagnostic.setLength(4 * 1024);
				}
				error = error + "\nBelow is extended diagnostic data which maybe truncated (check logs).\n"
						+ diagnostic.toString();
			}
			throw new IOException(error);
		}

		return resource.openInputStream();
	}

	@SuppressWarnings("null")
	private @Nullable EclipseClasspathFile findEclipseClasspathFile(Path classOutputPath) throws IOException {

		return eclipseClasspathFileCache.computeIfAbsent(classOutputPath, (Path p) -> {
			try {
				return EclipseClasspath.find(p);
			}
			catch (IOException e) {
				throw Throwables.sneakyThrow(e);
			}
		}).orElse(null);
	}

	private static String printAttempts(String error, String name, FileObject resource) {
		StringBuilder sb = new StringBuilder(error);
		return printAttempts(sb, name, resource, List.of(), "").toString();
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

	private interface ProjectPattern {

		String RESOURCES = "src/main/resources";

		String RESOURCES_TEST = "src/test/resources";

		List<String> relativeSourcePaths();

		record EclipseProjectPattern(List<String> relativeSourcePaths) implements ProjectPattern {
			// eclipse bug
			@Override
			public List<String> relativeSourcePaths() {
				return this.relativeSourcePaths;
			}
		}

	}

	@SuppressWarnings("ImmutableEnumChecker")
	private enum OutputPathPattern implements ProjectPattern {

		KAPT("/build/tmp/kapt3/classes/main/", List.of(RESOURCES)), //
		KAPT_TEST("/build/tmp/kapt3/classes/test/", List.of(RESOURCES_TEST)), //
		GRADLE("/build/classes/java/main/", List.of(RESOURCES)), //
		GRADLE_TEST("/build/classes/java/test/", List.of(RESOURCES_TEST)), //
		MAVEN("/target/classes/", List.of(RESOURCES)), //
		MAVEN_TEST("/target/test-classes/", List.of(RESOURCES_TEST)), //
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

		private final StandardLocation outputType;

		private OutputPathPattern(String endPath, List<String> relativeSourcePaths) {
			this(endPath, relativeSourcePaths, StandardLocation.CLASS_OUTPUT);
		}

		private OutputPathPattern(String endPath, List<String> relativeSourcePaths, StandardLocation outputType) {
			this.endPath = endPath;
			this.relativeSourcePaths = relativeSourcePaths;
			this.outputType = outputType;
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

		@Override
		public List<String> relativeSourcePaths() {
			return relativeSourcePaths;
		}

		public static OutputPathPattern find(URI uri) {
			return find(uri, StandardLocation.CLASS_OUTPUT);
		}

		public static OutputPathPattern find(URI uri, StandardLocation location) {
			String u = uri.toString().trim();
			for (var o : values()) {
				if (location != o.outputType) {
					continue;
				}
				if (o.matches(u)) {
					return o;
				}
			}
			return CWD;
		}

	}

	private static StringBuilder diagnosticDump(StringBuilder sb, ProcessingConfig config,
			List<Entry<String, String>> keys) {
		sb.append("Environment Info:").append("\n");
		Map<String, String> info = new LinkedHashMap<>();
		keys.forEach(e -> info.put(e.getKey(), e.getValue()));
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
			if (key.contains("secret") //
					|| key.contains("password") //
					|| key.contains("key") //
					|| key.contains("line.separator") //
					|| key.contains("vmargs") //
					|| key.contains("eclipse.commands") //
					|| key.startsWith("osgi") //
					|| key.startsWith("org.osgi")) {
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
