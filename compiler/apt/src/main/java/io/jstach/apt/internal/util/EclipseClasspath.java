package io.jstach.apt.internal.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.tools.StandardLocation;

import org.eclipse.jdt.annotation.Nullable;
import org.w3c.dom.Element;

/**
 * This class is to find Eclipse ".classpath" and parse them.
 */
public class EclipseClasspath {

	private final XmlHelper xml;

	protected EclipseClasspath(XmlHelper xml) {
		super();
		this.xml = xml;
	}

	public static EclipseClasspath of(Path classpathFile) throws IOException {
		try (var input = Files.newInputStream(classpathFile)) {
			var xml = XmlHelper.of(input);
			return new EclipseClasspath(xml);
		}
	}

	public static Optional<EclipseClasspathFile> find(Path childpath) throws IOException {
		var pf = findParentFile(childpath, ".classpath").orElse(null);
		if (pf == null) {
			return Optional.empty();
		}
		var eclipseClasspath = of(pf);
		var entries = eclipseClasspath.entries().toList();
		return Optional.of(new EclipseClasspathFile(pf, entries));

	}

	public record EclipseClasspathFile(Path classpathFile, List<ClasspathEntry> entries) {

		public Optional<ClasspathEntry> findEntry(Path classOutput, Path sourceOutput) {
			String output;
			String path;
			if (classOutput.isAbsolute() && sourceOutput.isAbsolute()) {
				var dir = classpathFile.getParent();
				if (dir == null) {
					return Optional.empty();
				}
				output = dir.relativize(classOutput).toString();
				path = dir.relativize(sourceOutput).toString();
			}
			else if (!classOutput.isAbsolute() && !sourceOutput.isAbsolute()) {
				output = classOutput.toString();
				path = sourceOutput.toString();
			}
			else {
				throw new IllegalArgumentException(
						"classOutput and sourceOutput should be both absolute or neither absolute");
			}
			return entries.stream().filter(ce -> output.equals(ce.output()) && path.equals(ce.path())).findFirst();
		}

		public Stream<String> findRelativeSourcePaths(@Nullable Path classOutput, @Nullable Path sourceOutput) {
			/*
			 * We are looking for the exact matching entry to determine if it is a test
			 * output.
			 */
			var entry = classOutput != null && sourceOutput != null ? findEntry(classOutput, sourceOutput).orElse(null)
					: null;
			/*
			 * If no match is found then we just return all source paths.
			 */
			if (entry == null) {
				return entries.stream().filter(ClasspathEntry::isSourcePath).map(ClasspathEntry::path).distinct();
			}
			/*
			 * If this is a test source path we need to make it have precedence
			 */
			Stream<ClasspathEntry> mainStream = entries.stream().filter(ce -> !ce.isTest());
			Stream<ClasspathEntry> testStream = entries.stream().filter(ce -> ce.isTest());
			Stream<ClasspathEntry> resolved;
			if (entry.isTest()) {
				resolved = Stream.<ClasspathEntry>concat(testStream, mainStream);
			}
			else {
				resolved = mainStream;
			}
			return resolved.filter(ClasspathEntry::isSourcePath).map(ClasspathEntry::path).distinct();
		}
	}

	public Stream<ClasspathEntry> entries() {
		return xml.findElements("//classpathentry").map(ClasspathEntry::of);
	}

	private static String nullToEmpty(@Nullable String s) {
		if (s == null) {
			return "";
		}
		return s;
	}

	public record ClasspathEntry(String output, String kind, String path, Map<String, String> attributes) {
		private static ClasspathEntry of(Element element) {
			String output = nullToEmpty(element.getAttribute("output"));
			String kind = nullToEmpty(element.getAttribute("kind"));
			String path = nullToEmpty(element.getAttribute("path"));
			var attributes = XmlHelper.toElementStream(element.getChildNodes()) //
					.filter(e -> "attributes".equals(e.getTagName())) //
					.flatMap(e -> XmlHelper.toElementStream(e.getChildNodes())) //
					.filter(e -> "attribute".equals(e.getTagName()));
			Map<String, String> ats = new LinkedHashMap<>();
			attributes.forEach(a -> {
				String name = nullToEmpty(a.getAttribute("name"));
				String value = nullToEmpty(a.getAttribute("value"));
				if (name != null && value != null) {
					ats.put(name, value);
				}
			});
			return new ClasspathEntry(output, kind, path, ats);
		}

		public @Nullable String path(StandardLocation location) {
			return switch (location) {
				case CLASS_OUTPUT -> output();
				case SOURCE_OUTPUT -> path();
				default -> throw new IllegalArgumentException("" + location);
			};
		}

		public boolean isSourcePath() {
			return !path.isEmpty() && "src".equals(kind);
		}

		public boolean isTest() {
			return "true".equals(attributes.get("test"));
		}
	}

	static Optional<Path> findParentFile(Path path, String fileName) {
		return createParentPathsStream(path).map(f -> f.resolve(fileName)).filter(p -> p.toFile().isFile()).findFirst();
	}

	static Stream<Path> createParentPathsStream(Path startingPath) {
		File file = startingPath.toAbsolutePath().toFile();
		if (file.isFile()) {
			file = file.getParentFile();
			if (file == null) {
				return Stream.empty();
			}
		}
		var sp = file.toPath();

		Iterable<Path> iterable = () -> new PathIterator(sp);
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	private static class PathIterator implements java.util.Iterator<Path> {

		private @Nullable Path currentPath;

		public PathIterator(Path startingPath) {
			this.currentPath = startingPath;
		}

		@Override
		public boolean hasNext() {
			return currentPath != null;
		}

		@Override
		public Path next() {
			Path nextPath = currentPath;
			if (nextPath == null) {
				throw new NoSuchElementException();
			}
			currentPath = nextPath.getParent();
			return nextPath;
		}

	}

}
