package io.jstach.apt.internal.util;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.junit.Test;

public class EclipseClasspathTest {

	private final String jskovXml = """
			<?xml version="1.0" encoding="UTF-8"?>
			<classpath>
			    <classpathentry kind="output" path="bin/default"/>
			    <classpathentry output="bin/main" kind="src" path="src/main/java">
			        <attributes>
			            <attribute name="gradle_scope" value="main"/>
			            <attribute name="gradle_used_by_scope" value="main,test"/>
			        </attributes>
			    </classpathentry>
			    <classpathentry output="bin/main" kind="src" path="src/main/resources">
			        <attributes>
			            <attribute name="gradle_scope" value="main"/>
			            <attribute name="gradle_used_by_scope" value="main,test"/>
			        </attributes>
			    </classpathentry>
			    <classpathentry output="bin/test" kind="src" path="src/test/java">
			        <attributes>
			            <attribute name="gradle_scope" value="test"/>
			            <attribute name="gradle_used_by_scope" value="test"/>
			            <attribute name="test" value="true"/>
			        </attributes>
			    </classpathentry>
			    <classpathentry output="bin/test" kind="src" path="src/test/resources">
			        <attributes>
			            <attribute name="gradle_scope" value="test"/>
			            <attribute name="gradle_used_by_scope" value="test"/>
			            <attribute name="test" value="true"/>
			        </attributes>
			    </classpathentry>
			    <classpathentry output="bin/eclipseApt" kind="src" path=".apt_generated">
			        <attributes>
			            <attribute name="gradle_scope" value="eclipseApt"/>
			            <attribute name="gradle_used_by_scope" value="eclipseApt"/>
			        </attributes>
			    </classpathentry>
			</classpath>
						""";

	@Test
	public void testEclipseClasspath() throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(jskovXml.getBytes(StandardCharsets.UTF_8));
		EclipseClasspath cp = new EclipseClasspath(XmlHelper.of(stream));
		var entries = cp.entries().map(o -> o.toString()).collect(Collectors.joining("\n"));
		String expected = """
				ClasspathEntry[output=, kind=output, path=bin/default, attributes={}]
				ClasspathEntry[output=bin/main, kind=src, path=src/main/java, attributes={gradle_scope=main, gradle_used_by_scope=main,test}]
				ClasspathEntry[output=bin/main, kind=src, path=src/main/resources, attributes={gradle_scope=main, gradle_used_by_scope=main,test}]
				ClasspathEntry[output=bin/test, kind=src, path=src/test/java, attributes={gradle_scope=test, gradle_used_by_scope=test, test=true}]
				ClasspathEntry[output=bin/test, kind=src, path=src/test/resources, attributes={gradle_scope=test, gradle_used_by_scope=test, test=true}]
				ClasspathEntry[output=bin/eclipseApt, kind=src, path=.apt_generated, attributes={gradle_scope=eclipseApt, gradle_used_by_scope=eclipseApt}]""";

		assertEquals(expected, entries);

		Path p = Path.of("/some/parent/.classpath");
		EclipseClasspath.EclipseClasspathFile f = new EclipseClasspath.EclipseClasspathFile(p, cp.entries().toList());
		var sourcePaths = f.findRelativeSourcePaths(null, null).toList();
		expected = "[src/main/java, src/main/resources, src/test/java, src/test/resources, .apt_generated]";
		assertEquals(expected, sourcePaths.toString());
		sourcePaths = f
				.findRelativeSourcePaths(Path.of("/some/parent/bin/eclipseApt"), Path.of("/some/parent/.apt_generated"))
				.toList();
		expected = "[src/main/java, src/main/resources, .apt_generated]";
		assertEquals(expected, sourcePaths.toString());
		/*
		 * We expect the test directories to be first since a test output was found
		 */
		sourcePaths = f.findRelativeSourcePaths(Path.of("/some/parent/bin/test"), Path.of("/some/parent/src/test/java"))
				.toList();
		expected = "[src/test/java, src/test/resources, src/main/java, src/main/resources, .apt_generated]";
		assertEquals(expected, sourcePaths.toString());

		sourcePaths = f.findRelativeSourcePaths(Path.of("bin/test"), Path.of("src/test/java")).toList();
		expected = "[src/test/java, src/test/resources, src/main/java, src/main/resources, .apt_generated]";
		assertEquals(expected, sourcePaths.toString());
	}

}
