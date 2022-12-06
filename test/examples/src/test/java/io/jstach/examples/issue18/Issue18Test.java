package io.jstach.examples.issue18;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.jstach.jstache.JStache;

/**
 * https://github.com/jstachio/jstachio/issues/18
 *
 * @author agentgt
 * @author Allsimon
 */
public class Issue18Test {

	@JStache(template = """
			bugged "here"
			""")
	public record BugReport() {
	}

	@Test
	public void testName() throws Exception {
		String actual = BugReportRenderer.of().execute(new BugReport());
		assertEquals("bugged \"here\"\n", actual);

		String template = BugReportRenderer.TEMPLATE_STRING;
		assertEquals("bugged \"here\"\n", template);

		assertEquals(template, BugReportRenderer.of().templateString());
	}

}
