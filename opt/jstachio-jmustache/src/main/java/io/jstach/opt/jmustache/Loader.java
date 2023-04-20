package io.jstach.opt.jmustache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.jstachio.TemplateInfo;
import io.jstach.jstachio.TemplateInfo.TemplateSource;

class Loader {

	private final Logger logger;

	private final String sourcePath;

	private final long initTime;

	public Loader(Logger logger, String sourcePath, long initTime) {
		super();
		this.logger = logger;
		this.sourcePath = sourcePath;
		this.initTime = initTime;
	}

	protected @Nullable Reader open(TemplateInfo template, boolean broken) throws IOException {
		TemplateSource source = template.templateSource();
		String templateString = template.templateString();
		String templatePath = template.normalizePath();

		boolean changed = template.lastLoaded() > 0;

		return switch (source) {
			case STRING -> {
				yield new StringReader(templateString);
			}
			case RESOURCE -> {
				yield resource(templatePath, broken, changed);
			}
		};
	}

	private Reader resource(String templatePath, boolean broken, boolean changed) throws IOException {
		var path = path(templatePath);
		InputStream stream;
		boolean _broken = changed || broken;
		if ((_broken && path.toFile().isFile()) || path.toFile().lastModified() > initTime) {
			stream = this.openFile(path);
		}
		else if (broken) {
			stream = this.openResource(templatePath);
		}
		else {
			return null;
		}
		return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
	}

	protected Reader openPartial(String templatePath) throws IOException {
		var reader = resource(templatePath, true, true);
		Objects.requireNonNull(reader);
		return reader;
	}

	Path path(String templatePath) {
		return Path.of(sourcePath, templatePath);
	}

	protected InputStream openFile(Path path) throws IOException {
		InputStream is = Files.newInputStream(path);
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "Using JMustache. template:" + "file " + path);
		}
		return is;
	}

	protected InputStream openResource(String templatePath) throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream is = loader.getResourceAsStream(templatePath);
		if (is == null) {
			throw new IOException("template not found. template: " + templatePath);
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "Using JMustache. template:" + "classpath " + templatePath);
		}
		return is;
	}

	public String getSourcePath() {
		return sourcePath;
	}

}
