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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
			FileObject dummy = env.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", "dummy");

			/*
			 * Aka the CWD
			 */
			Path projectPath;

			if (config.isGradle()) {
				if (config.isDebug()) {
					config.debug("Looks like we are using Gradle incremental. dummy: " + dummy.toUri());
				}
				// build/classes/java/main/dummy
				projectPath = Paths.get(dummy.toUri()).getParent().getParent().getParent().getParent().getParent();

			}
			else {
				if (config.isDebug()) {
					config.debug("Looks like we are using Eclipse incremental. dummy: " + dummy.toUri());
				}
				// target/classes/dummy
				projectPath = Paths.get(dummy.toUri()).getParent().getParent().getParent();
			}
			String resourceName = name;
			List<Path> fullPaths = config.resourcesPaths().stream()
					.map(rp -> resolvePath(projectPath, rp, resourceName)).toList();

			for (Path fullPath : fullPaths) {
				if (config.isDebug()) {
					config.debug("File not found with Filer. Trying direct file access. name:" + resourceName
							+ ", path: " + fullPath + ", dummy: " + dummy.toUri());
				}
				if (Files.isReadable(fullPath)) {
					return Files.newInputStream(fullPath);
				}
			}
			StringBuilder sb = new StringBuilder();
			sb.append("Failed to find template resource: '").append(name).append("'. Tried the following locations: ");
			sb.append("'").append(resource.toUri()).append("'");
			for (Path p : fullPaths) {
				sb.append(", '").append(p.toString()).append("'");
			}

			throw new IOException(sb.toString());
		}

		return resource.openInputStream();
	}

	private static Path resolvePath(Path projectPath, String resourcePath, String name) {
		Path filePath = Path.of(resourcePath, name);
		return filePath.isAbsolute() ? filePath : projectPath.resolve(filePath);
	}

	Charset charset() {
		return config.charset();
	}

}
