package io.jstach.apt;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class IncrementalGradleTest {

	@Test
	public void test() throws IOException {
		var is = GenerateRendererProcessor.class
				.getResourceAsStream("/META-INF/gradle/incremental.annotation.processors");
		InputStreamReader r = new InputStreamReader(is, StandardCharsets.UTF_8);
		StringWriter sw = new StringWriter();
		r.transferTo(sw);
		String expected = GenerateRendererProcessor.class.getCanonicalName() + ",dynamic";
		assertEquals(expected, sw.toString());
	}

}
