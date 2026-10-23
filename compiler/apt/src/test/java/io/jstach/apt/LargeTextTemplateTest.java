package io.jstach.apt;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import io.jstach.jstachio.Template;

public class LargeTextTemplateTest {

	@Rule
	public TemporaryFolder temporary = new TemporaryFolder();

	@Test
	public void testLargeAsciiText() throws Exception {
		assertTemplate("123456789\n".repeat(6554));
	}

	@Test
	public void testLargeTextWithoutNewlines() throws Exception {
		assertTemplate("x".repeat(70000));
	}

	@Test
	public void testModifiedUtf8Limit() throws Exception {
		assertTemplate("\u0800".repeat(22000));
	}

	@Test
	public void testSupplementaryCharacterAtChunkBoundary() throws Exception {
		assertTemplate("a".repeat(16383) + "\ud83d\ude00" + "z".repeat(70000));
	}

	@Test
	public void testEscapesAtChunkBoundaries() throws Exception {
		assertTemplate("a".repeat(16383) + "\"\\\n" + "\\".repeat(40000));
	}

	@Test
	public void testSmallTextIsUnchanged() throws Exception {
		assertTemplate("small \"quoted\" text\\with\nnewlines and \ud83d\ude00");
	}

	@Test
	public void testLargeTextAroundVariable() throws Exception {
		String before = "x".repeat(70000);
		String after = "y".repeat(70000);
		assertTemplate(before + "{{value}}" + after, before + "VALUE" + after);
	}

	private void assertTemplate(String text) throws Exception {
		assertTemplate(text, text);
	}

	@SuppressWarnings("unchecked")
	private void assertTemplate(String text, String expected) throws Exception {
		Path directory = temporary.newFolder().toPath();
		Path source = directory.resolve("LargeModel.java");
		Files.writeString(directory.resolve("large.mustache"), text, StandardCharsets.UTF_8);
		Files.writeString(source,
				"@io.jstach.jstache.JStache(path=\"large.mustache\") " + "public record LargeModel(String value) {}",
				StandardCharsets.UTF_8);
		var compiler = ToolProvider.getSystemJavaCompiler();
		var diagnostics = new DiagnosticCollector<JavaFileObject>();
		try (var files = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8)) {
			var options = List.of("-classpath", System.getProperty("java.class.path"), "-d", directory.toString(), "-s",
					directory.toString(), "-Ajstache.resourcesPath=" + directory);
			var task = compiler.getTask(null, files, diagnostics, options, null, files.getJavaFileObjects(source));
			task.setProcessors(List.of(new GenerateRendererProcessor()));
			boolean success = task.call();
			assertTrue(diagnostics.getDiagnostics().toString(), success);
		}
		try (var loader = new URLClassLoader(new URL[] { directory.toUri().toURL() }, getClass().getClassLoader())) {
			Object model = loader.loadClass("LargeModel").getConstructor(String.class).newInstance("VALUE");
			Template<Object> template = (Template<Object>) loader.loadClass("LargeModelRenderer").getConstructor()
					.newInstance();
			assertEquals(expected, template.execute(model));
			var bytes = new ByteArrayOutputStream();
			template.write(model, bytes);
			assertArrayEquals(expected.getBytes(StandardCharsets.UTF_8), bytes.toByteArray());
		}
	}

}
